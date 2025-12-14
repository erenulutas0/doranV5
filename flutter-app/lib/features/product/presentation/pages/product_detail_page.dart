import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';

import '../../../../core/providers/product_provider.dart';
import '../../../../core/providers/cart_provider.dart';
import '../../../../core/providers/auth_provider.dart';
import '../../../../core/utils/price_formatter.dart';
import '../../../../core/services/api_service.dart';
import '../../../../core/models/review_model.dart';
import '../../../../core/models/rating_summary_model.dart';

class ProductDetailPage extends StatefulWidget {
  final String productId;

  const ProductDetailPage({
    super.key,
    required this.productId,
  });

  @override
  State<ProductDetailPage> createState() => _ProductDetailPageState();
}

class _ProductDetailPageState extends State<ProductDetailPage> {
  final ApiService _apiService = ApiService();
  List<Review> _reviews = [];
  RatingSummary? _ratingSummary;
  bool _isLoadingReviews = true;
  String? _reviewsError;
  // Spam beğeniyi engellemek için: hangi review'lar beğenildi ve hangileri loading
  final Set<String> _likedReviewIds = <String>{};
  final Set<String> _loadingHelpfulReviewIds = <String>{};

  @override
  void initState() {
    super.initState();
    _loadReviewsAndRating();
  }

  Future<void> _loadReviewsAndRating() async {
    setState(() {
      _isLoadingReviews = true;
      _reviewsError = null;
    });

    try {
      // Try to load reviews and rating summary separately
      // If one fails, the other should still work
      List<Review> reviews = [];
      RatingSummary? ratingSummary;
      
      // Load reviews with userId if authenticated
      try {
        final authProvider = Provider.of<AuthProvider>(context, listen: false);
        final userId = authProvider.userId;
        
        reviews = await _apiService.getReviewsByProductId(widget.productId, userId: userId);
        print('✅ Loaded ${reviews.length} reviews for product ${widget.productId}');
        
        // Kullanıcının beğendiği review'ları _likedReviewIds Set'ine ekle
        final likedIds = reviews
            .where((review) => review.likedByUser)
            .map((review) => review.id)
            .toSet();
        
        print('👍 Backend\'den gelen likedByUser durumu:');
        for (var review in reviews) {
          if (review.likedByUser) {
            print('   Review ${review.id}: likedByUser = true');
          }
        }
        print('   Toplam beğenilen review sayısı: ${likedIds.length}');
        
        // Debug: Print first review if exists
        if (reviews.isNotEmpty) {
          final firstReview = reviews.first;
          print('   First review: ${firstReview.userName}, rating: ${firstReview.rating}, likedByUser: ${firstReview.likedByUser}, comment: ${firstReview.comment?.substring(0, firstReview.comment!.length > 50 ? 50 : firstReview.comment!.length)}...');
        }
        
        setState(() {
          // ÖNCE temizle, sonra yeni değerleri ekle
          _likedReviewIds.clear();
          _likedReviewIds.addAll(likedIds);
          print('✅ _likedReviewIds güncellendi: ${_likedReviewIds.length} adet');
        });
      } catch (e) {
        print('❌ Error loading reviews: $e');
        reviews = [];
      }
      
      // Try to load rating summary, but don't fail if it errors
      try {
        ratingSummary = await _apiService.getRatingSummary(widget.productId);
        print('✅ Loaded rating summary: avg=${ratingSummary?.averageRating}, total=${ratingSummary?.totalReviews}');
        if (ratingSummary != null) {
          print('   Star counts: 5★=${ratingSummary.star5Count}, 4★=${ratingSummary.star4Count}, 3★=${ratingSummary.star3Count}, 2★=${ratingSummary.star2Count}, 1★=${ratingSummary.star1Count}');
        }
      } catch (e) {
        // Rating summary error is not critical, we can still show reviews
        print('⚠️ Rating summary error (non-critical): $e');
      }
      
      setState(() {
        _reviews = reviews;
        _ratingSummary = ratingSummary;
        _isLoadingReviews = false;
      });
    } catch (e) {
      // If error is 404 or empty response, it means no reviews yet - that's okay
      final errorStr = e.toString();
      print('❌ Error in _loadReviewsAndRating: $errorStr');
      if (errorStr.contains('404') || errorStr.contains('Failed to load reviews')) {
        setState(() {
          _reviews = [];
          _ratingSummary = null;
          _isLoadingReviews = false;
          // Don't set error for 404 - it just means no reviews yet
        });
      } else {
        setState(() {
          _reviewsError = errorStr;
          _isLoadingReviews = false;
        });
      }
    }
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final difference = now.difference(date);

    if (difference.inDays == 0) {
      return 'Bugün';
    } else if (difference.inDays == 1) {
      return 'Dün';
    } else if (difference.inDays < 7) {
      return '${difference.inDays} gün önce';
    } else if (difference.inDays < 30) {
      final weeks = (difference.inDays / 7).floor();
      return '$weeks hafta önce';
    } else if (difference.inDays < 365) {
      final months = (difference.inDays / 30).floor();
      return '$months ay önce';
    } else {
      final years = (difference.inDays / 365).floor();
      return '$years yıl önce';
    }
  }

  @override
  Widget build(BuildContext context) {
    final productProvider = context.watch<ProductProvider>();
    final product = productProvider.getProductById(widget.productId);

    if (product == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Product Details')),
        body: const Center(child: Text('Product not found')),
      );
    }

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          // App Bar with Image
          SliverAppBar(
            expandedHeight: 300,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              background: CachedNetworkImage(
                imageUrl: product.imageUrl ?? 'https://via.placeholder.com/400',
                fit: BoxFit.cover,
                placeholder: (context, url) => Container(
                  color: Colors.grey[200],
                  child: const Center(child: CircularProgressIndicator()),
                ),
              ),
            ),
            leading: IconButton(
              icon: Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.9),
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.arrow_back, color: Colors.black),
              ),
              onPressed: () => context.pop(),
            ),
            actions: [
              IconButton(
                icon: Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.9),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.favorite_border, color: Colors.black),
                ),
                onPressed: () {},
              ),
            ],
          ),

          // Product Details
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Product Name
                  Text(
                    product.name,
                    style: Theme.of(context).textTheme.headlineSmall,
                  ),
                  const SizedBox(height: 8),

                  // Rating Section - Geliştirilmiş
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.3),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Ürün Değerlendirmesi',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(height: 12),
                        Row(
                          children: [
                            // Büyük rating gösterimi
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  _ratingSummary != null 
                                      ? '${_ratingSummary!.averageRating.toStringAsFixed(1)}' 
                                      : (product.rating != null ? '${product.rating!.toStringAsFixed(1)}' : '0.0'),
                                  style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                                    fontWeight: FontWeight.bold,
                                    color: Theme.of(context).colorScheme.primary,
                                  ),
                                ),
                                RatingBarIndicator(
                                  rating: _ratingSummary != null 
                                      ? _ratingSummary!.averageRating 
                                      : (product.rating ?? 0.0),
                                  itemBuilder: (context, index) => const Icon(
                                    Icons.star,
                                    color: Colors.amber,
                                  ),
                                  itemCount: 5,
                                  itemSize: 24,
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  _ratingSummary != null 
                                      ? '${_ratingSummary!.totalReviews} Değerlendirme'
                                      : '${product.reviewCount ?? 0} Değerlendirme',
                                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                    color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                                  ),
                                ),
                              ],
                            ),
                            const Spacer(),
                            // Yıldız dağılımı (database'den gelen veriler - her zaman göster)
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.end,
                              children: [
                                _buildStarDistribution(5, _ratingSummary?.getStarPercentage(5) ?? 0.0, context),
                                _buildStarDistribution(4, _ratingSummary?.getStarPercentage(4) ?? 0.0, context),
                                _buildStarDistribution(3, _ratingSummary?.getStarPercentage(3) ?? 0.0, context),
                                _buildStarDistribution(2, _ratingSummary?.getStarPercentage(2) ?? 0.0, context),
                                _buildStarDistribution(1, _ratingSummary?.getStarPercentage(1) ?? 0.0, context),
                              ],
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 24),

                  // Price
                  Row(
                    children: [
                      if (product.discountPrice != null)
                        Text(
                          PriceFormatter.format(product.price),
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            decoration: TextDecoration.lineThrough,
                            color: Theme.of(context).colorScheme.onSurface.withOpacity(0.5),
                          ),
                        ),
                      if (product.discountPrice != null) const SizedBox(width: 8),
                      Text(
                        PriceFormatter.format(product.discountPrice ?? product.price),
                        style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                          color: Theme.of(context).colorScheme.primary,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 24),

                  // Description
                  if (product.description != null) ...[
                    Text(
                      'Description',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      product.description!,
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                    const SizedBox(height: 24),
                  ],

                  // Product Reviews Section
                  _buildReviewsSection(context, product),
                  const SizedBox(height: 24),

                  // Payment Options Section
                  _buildPaymentOptionsSection(context),
                  const SizedBox(height: 100), // Bottom navigation için boşluk
                ],
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 10,
              offset: const Offset(0, -2),
            ),
          ],
        ),
        child: SafeArea(
          child: Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: () {},
                  child: const Text('Add to Wishlist'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                flex: 2,
                child: Consumer<CartProvider>(
                  builder: (context, cartProvider, child) {
                    return ElevatedButton(
                      onPressed: () {
                        cartProvider.addItem(product);
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(
                            content: Text('Product added to cart'),
                            duration: Duration(seconds: 2),
                          ),
                        );
                      },
                      child: const Text('Add to Cart'),
                    );
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStarDistribution(int stars, double percentage, BuildContext context) {
    // Percentage'i 0-1 aralığına normalize et
    final normalizedPercentage = (percentage / 100.0).clamp(0.0, 1.0);
    
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            '$stars',
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
              fontWeight: FontWeight.w500,
            ),
          ),
          const SizedBox(width: 4),
          const Icon(Icons.star, size: 12, color: Colors.amber),
          const SizedBox(width: 8),
          Container(
            width: 80,
            height: 8,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.5),
              borderRadius: BorderRadius.circular(4),
            ),
            child: Stack(
              children: [
                // Dolgu çubuğu
                FractionallySizedBox(
                  alignment: Alignment.centerLeft,
                  widthFactor: normalizedPercentage,
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.amber,
                      borderRadius: BorderRadius.circular(4),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildReviewsSection(BuildContext context, product) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Ürün Yorumları',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            if (_reviews.isNotEmpty)
              TextButton(
                onPressed: () {
                  // Tüm yorumları görüntüle
                },
                child: const Text('Tümünü Gör'),
              ),
          ],
        ),
        const SizedBox(height: 12),
        if (_isLoadingReviews)
          const Center(
            child: Padding(
              padding: EdgeInsets.all(24.0),
              child: CircularProgressIndicator(),
            ),
          )
        else if (_reviewsError != null)
          Center(
            child: Padding(
              padding: const EdgeInsets.all(24.0),
              child: Text(
                'Yorumlar yüklenemedi',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Theme.of(context).colorScheme.error,
                ),
              ),
            ),
          )
        else if (_reviews.isEmpty)
          Center(
            child: Padding(
              padding: const EdgeInsets.all(24.0),
              child: Text(
                'Henüz yorum yok',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                ),
              ),
            ),
          )
        else
          ..._reviews
              .where((review) {
                // Comment kontrolü - null, boş veya sadece whitespace olmamalı
                final comment = review.comment;
                return comment != null && comment.trim().isNotEmpty;
              })
              .take(10)
              .map((review) => _buildReviewCard(context, review)),
      ],
    );
  }

  Widget _buildReviewCard(BuildContext context, Review review) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surface,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: Theme.of(context).dividerColor,
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Kullanıcı avatarı
              CircleAvatar(
                backgroundColor: Theme.of(context).colorScheme.primary.withOpacity(0.2),
                radius: 20,
                child: Text(
                  review.userName.isNotEmpty ? review.userName[0].toUpperCase() : '?',
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.primary,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            review.userName,
                            style: Theme.of(context).textTheme.titleSmall?.copyWith(
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                        // Yıldız sayısını göster
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: Theme.of(context).colorScheme.primaryContainer.withOpacity(0.3),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              const Icon(
                                Icons.star,
                                color: Colors.amber,
                                size: 16,
                              ),
                              const SizedBox(width: 4),
                              Text(
                                '${review.rating}',
                                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                  fontWeight: FontWeight.bold,
                                  color: Colors.amber.shade700,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        RatingBarIndicator(
                          rating: review.rating.toDouble(),
                          itemBuilder: (context, index) => const Icon(
                            Icons.star,
                            color: Colors.amber,
                            size: 14,
                          ),
                          itemCount: 5,
                          itemSize: 14,
                        ),
                        const SizedBox(width: 8),
                        Text(
                          _formatDate(review.createdAt),
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: Theme.of(context).colorScheme.onSurface.withOpacity(0.5),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
          if (review.comment != null && review.comment!.isNotEmpty) ...[
            const SizedBox(height: 12),
            Text(
              review.comment!,
              style: Theme.of(context).textTheme.bodyMedium,
            ),
            const SizedBox(height: 12),
            // Beğenme butonu ve sayısı
            Consumer<AuthProvider>(
              builder: (context, authProvider, _) {
                final bool isAuthenticated = authProvider.isAuthenticated;
                final String? userId = authProvider.userId;
                
                return Row(
                  children: [
                    // Beğenme butonu (SADECE giriş yapmış kullanıcılar için)
                    if (isAuthenticated && userId != null && userId.isNotEmpty)
                      InkWell(
                        onTap: _likedReviewIds.contains(review.id) || _loadingHelpfulReviewIds.contains(review.id)
                            ? null // Disable if already liked or loading
                            : () async {
                                // Spam beğeniyi engelle: loading state ekle
                                setState(() {
                                  _loadingHelpfulReviewIds.add(review.id);
                                });

                                try {
                                  // Debug: userId kontrolü
                                  print('🔍 Beğeni İsteği:');
                                  print('   Review ID: ${review.id}');
                                  print('   User ID: $userId');
                                  print('   User ID boş mu?: ${userId == null || userId.isEmpty}');
                                  
                                  if (userId == null || userId.isEmpty) {
                                    throw Exception('User ID is required to like a review');
                                  }
                                  
                                  print('📤 API çağrısı yapılıyor...');
                                  final updatedReview = await _apiService.markReviewAsHelpful(review.id, userId);
                                  print('✅ API çağrısı başarılı!');
                                  print('   Güncellenmiş helpfulCount: ${updatedReview.helpfulCount}');
                                  
                                  // Review'ları yeniden yükle (cache'den güncel veriyi al)
                                  final authProvider = Provider.of<AuthProvider>(context, listen: false);
                                  final currentUserId = authProvider.userId;
                                  print('🔄 Reviewlar yeniden yükleniyor...');
                                  final refreshedReviews = await _apiService.getReviewsByProductId(
                                    widget.productId,
                                    userId: currentUserId,
                                  );
                                  print('✅ Reviewlar yüklendi: ${refreshedReviews.length} adet');
                                  
                                  // Kullanıcının beğendiği review'ları güncelle
                                  final likedIds = refreshedReviews
                                      .where((r) => r.likedByUser)
                                      .map((r) => r.id)
                                      .toSet();
                                  print('👍 Beğenilen reviewlar: ${likedIds.length} adet');
                                  
                                  // Güncellenmiş review'ı bul
                                  final updatedReviewFromList = refreshedReviews.firstWhere(
                                    (r) => r.id == review.id,
                                    orElse: () => updatedReview,
                                  );
                                  print('📊 Güncellenmiş review helpfulCount: ${updatedReviewFromList.helpfulCount}');
                                  
                                  setState(() {
                                    // Beğenildi olarak işaretle
                                    _likedReviewIds.add(review.id);
                                    _likedReviewIds.addAll(likedIds);
                                    _loadingHelpfulReviewIds.remove(review.id);
                                    
                                    // Tüm review'ları güncelle (güncel helpfulCount ile)
                                    _reviews = refreshedReviews;
                                  });
                                  
                                  print('✅ State güncellendi!');
                                  
                                  if (context.mounted) {
                                    ScaffoldMessenger.of(context).showSnackBar(
                                      SnackBar(
                                        content: Text('Yorum beğenildi! (${updatedReviewFromList.helpfulCount} beğeni)'),
                                        duration: const Duration(seconds: 2),
                                      ),
                                    );
                                  }
                                } catch (e) {
                                  setState(() {
                                    _loadingHelpfulReviewIds.remove(review.id);
                                    
                                    // Eğer "already marked" hatası alırsak, beğenildi olarak işaretle
                                    if (e.toString().contains('already marked') || 
                                        e.toString().contains('409')) {
                                      _likedReviewIds.add(review.id);
                                    }
                                  });
                                  
                                  if (context.mounted) {
                                    String errorMessage = 'Hata: Yorum beğenilemedi';
                                    String errorDetails = e.toString();
                                    
                                    // Hata detaylarını logla
                                    print('❌ Beğeni hatası: $errorDetails');
                                    
                                    if (errorDetails.contains('already marked') || 
                                        errorDetails.contains('409')) {
                                      errorMessage = 'Bu yorumu zaten beğenmişsiniz';
                                    } else if (errorDetails.contains('not found') || 
                                               errorDetails.contains('404')) {
                                      errorMessage = 'Yorum bulunamadı';
                                    } else {
                                      errorMessage = 'Beğeni kaydedilemedi. Lütfen tekrar deneyin.';
                                    }
                                    
                                    ScaffoldMessenger.of(context).showSnackBar(
                                      SnackBar(
                                        content: Text('$errorMessage\n\nDetay: ${errorDetails.substring(0, errorDetails.length > 100 ? 100 : errorDetails.length)}'),
                                        backgroundColor: Theme.of(context).colorScheme.error,
                                        duration: const Duration(seconds: 5),
                                      ),
                                    );
                                  }
                                }
                              },
                        child: Opacity(
                          opacity: _likedReviewIds.contains(review.id) || _loadingHelpfulReviewIds.contains(review.id) ? 0.5 : 1.0,
                          child: Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                            decoration: BoxDecoration(
                              color: _likedReviewIds.contains(review.id)
                                  ? Theme.of(context).colorScheme.primary.withOpacity(0.2)
                                  : Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.5),
                              borderRadius: BorderRadius.circular(20),
                              border: Border.all(
                                color: _likedReviewIds.contains(review.id)
                                    ? Theme.of(context).colorScheme.primary
                                    : Theme.of(context).dividerColor,
                                width: 1,
                              ),
                            ),
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                if (_loadingHelpfulReviewIds.contains(review.id))
                                  SizedBox(
                                    width: 16,
                                    height: 16,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                      valueColor: AlwaysStoppedAnimation<Color>(
                                        Theme.of(context).colorScheme.primary,
                                      ),
                                    ),
                                  )
                                else
                                  Icon(
                                    _likedReviewIds.contains(review.id)
                                        ? Icons.thumb_up
                                        : Icons.thumb_up_outlined,
                                    size: 16,
                                    color: Theme.of(context).colorScheme.primary,
                                  ),
                                const SizedBox(width: 6),
                                Text(
                                  _likedReviewIds.contains(review.id)
                                      ? 'Beğenildi'
                                      : 'Yardımcı Oldu',
                                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                    color: Theme.of(context).colorScheme.primary,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                ),
                    const SizedBox(width: 12),
                    // Beğeni sayısı (her zaman gösterilir)
                    Text(
                      review.helpfulCount > 0 
                          ? '${review.helpfulCount} kişi bunu beğendi'
                          : 'Henüz beğeni yok',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                      ),
                    ),
                  ],
                );
              },
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildPaymentOptionsSection(BuildContext context) {
    // Payment options database'den gelecek (şimdilik boş gösteriyoruz)
    // TODO: Product-service'den payment options çekilecek
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.3),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.payment,
                color: Theme.of(context).colorScheme.primary,
                size: 24,
              ),
              const SizedBox(width: 8),
              Text(
                'Ödeme Seçenekleri',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          // Payment options database'den gelecek
          // Şimdilik bilgi mesajı gösteriyoruz
          Center(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Text(
                'Ödeme seçenekleri yakında eklenecek',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

