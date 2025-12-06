import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:provider/provider.dart';

import '../../../../core/models/product_model.dart';
import '../../../../core/utils/price_formatter.dart';
import '../../../../core/providers/cart_provider.dart';

class ProductGrid extends StatelessWidget {
  final List<Product> products;

  const ProductGrid({
    super.key,
    required this.products,
  });

  @override
  Widget build(BuildContext context) {
    // Responsive grid: Ekran boyutuna göre dinamik ayarlama
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallPhone = screenWidth < 400; // iPhone SE gibi küçük telefonlar
    final isMobile = screenWidth < 600;
    final crossAxisCount = isMobile ? 2 : 3;
    
    // Ekran boyutuna göre padding ve aspect ratio
    double horizontalPadding;
    double gridSpacing;
    double childAspectRatio;
    
    if (isSmallPhone) {
      // iPhone SE gibi çok küçük ekranlar (375px)
      horizontalPadding = 12.0;
      gridSpacing = 8.0;
      childAspectRatio = 0.72; // Daha kısa kartlar
    } else if (isMobile) {
      // Normal mobil (400-600px) - iPhone 14 Pro Max dahil
      horizontalPadding = 16.0;
      gridSpacing = 10.0;
      childAspectRatio = 0.75; // Daha kısa kartlar
    } else {
      // Tablet/Desktop
      horizontalPadding = 20.0;
      gridSpacing = 16.0;
      childAspectRatio = 0.78; // Daha kısa kartlar
    }
    
    return SliverPadding(
      padding: EdgeInsets.symmetric(horizontal: horizontalPadding),
      sliver: SliverGrid(
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: crossAxisCount,
          childAspectRatio: childAspectRatio,
          crossAxisSpacing: gridSpacing,
          mainAxisSpacing: gridSpacing,
        ),
        delegate: SliverChildBuilderDelegate(
          (context, index) {
            final product = products[index];
            return _ProductCard(
              product: product,
              isMobile: isMobile,
              isSmallPhone: isSmallPhone,
            );
          },
          childCount: products.length,
        ),
      ),
    );
  }
}

class _ProductCard extends StatefulWidget {
  final Product product;
  final bool isMobile;
  final bool isSmallPhone;

  const _ProductCard({
    required this.product,
    this.isMobile = false,
    this.isSmallPhone = false,
  });

  @override
  State<_ProductCard> createState() => _ProductCardState();
}

class _ProductCardState extends State<_ProductCard>
    with TickerProviderStateMixin {
  bool _showAddedFeedback = false;
  bool _isFavorite = false;
  late AnimationController _addToCartAnimationController;
  late AnimationController _favoriteAnimationController;
  late Animation<double> _scaleAnimation;
  late Animation<double> _favoriteScaleAnimation;

  @override
  void initState() {
    super.initState();
    _addToCartAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 200),
    );
    _favoriteAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );
    _scaleAnimation = Tween<double>(begin: 1.0, end: 1.15).animate(
      CurvedAnimation(parent: _addToCartAnimationController, curve: Curves.easeOut),
    );
    _favoriteScaleAnimation = Tween<double>(begin: 1.0, end: 1.3).animate(
      CurvedAnimation(parent: _favoriteAnimationController, curve: Curves.elasticOut),
    );
  }

  @override
  void dispose() {
    _addToCartAnimationController.dispose();
    _favoriteAnimationController.dispose();
    super.dispose();
  }

  void _handleFavorite() {
    setState(() {
      _isFavorite = !_isFavorite;
    });
    
    // Güzel animasyon efekti
    _favoriteAnimationController.forward().then((_) {
      _favoriteAnimationController.reverse();
    });
  }

  void _handleAddToCart(BuildContext context) {
    final cartProvider = context.read<CartProvider>();
    cartProvider.addItem(widget.product);
    
    // Animasyon
    _addToCartAnimationController.forward().then((_) {
      _addToCartAnimationController.reverse();
    });
    
    // Geri bildirim göster
    setState(() {
      _showAddedFeedback = true;
    });
    
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) {
        setState(() {
          _showAddedFeedback = false;
        });
      }
    });
    
    // Snackbar göster
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: const Row(
          children: [
            Icon(Icons.check_circle, color: Colors.white),
            SizedBox(width: 8),
            Text('Added to Cart'),
          ],
        ),
        backgroundColor: Colors.green,
        duration: const Duration(seconds: 2),
        behavior: SnackBarBehavior.floating,
        margin: const EdgeInsets.all(16),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => context.push('/product/${widget.product.id}'),
      child: Container(
        decoration: BoxDecoration(
          color: Theme.of(context).cardColor,
          borderRadius: BorderRadius.circular(widget.isSmallPhone ? 10 : widget.isMobile ? 12 : 16),
          border: Border.all(
            color: Theme.of(context).dividerColor,
            width: 1,
          ),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(
                Theme.of(context).brightness == Brightness.dark ? 0.3 : 0.05,
              ),
              blurRadius: 6,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        clipBehavior: Clip.antiAlias, // İçeriği kes
        child: Stack(
          children: [
            _buildCompactCard(context),
          ],
        ),
      ),
    );
  }

  Widget _buildCompactCard(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      mainAxisSize: MainAxisSize.min, // Boş alan bırakma
      children: [
        // Product Image - Küçültülmüş resim
        AspectRatio(
          aspectRatio: widget.isSmallPhone ? 1.3 : widget.isMobile ? 1.4 : 1.5, // Daha küçük resim oranı
          child: ClipRRect(
            borderRadius: BorderRadius.vertical(
              top: Radius.circular(widget.isSmallPhone ? 10 : widget.isMobile ? 12 : 16),
            ),
            child: Stack(
              children: [
                CachedNetworkImage(
                  imageUrl: widget.product.imageUrl ?? 'https://via.placeholder.com/200',
                  width: double.infinity,
                  height: double.infinity,
                  fit: BoxFit.cover,
                  placeholder: (context, url) => Container(
                    color: Theme.of(context).colorScheme.surfaceVariant,
                    child: Center(
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                    ),
                  ),
                  errorWidget: (context, url, error) => Container(
                    color: Theme.of(context).colorScheme.surfaceVariant,
                    child: Icon(
                      Icons.image_not_supported,
                      color: Theme.of(context).colorScheme.onSurfaceVariant,
                      size: 24,
                    ),
                  ),
                ),
                // Add to Cart Button - Sol üst köşe (Mor, şık ve küçük)
                Positioned(
                  top: 6,
                  left: 6,
                  child: ScaleTransition(
                    scale: _scaleAnimation,
                    child: Material(
                      color: Colors.transparent,
                      child: InkWell(
                        onTap: () => _handleAddToCart(context),
                        borderRadius: BorderRadius.circular(50),
                        customBorder: const CircleBorder(),
                        child: Container(
                          width: widget.isSmallPhone ? 32 : widget.isMobile ? 36 : 40,
                          height: widget.isSmallPhone ? 32 : widget.isMobile ? 36 : 40,
                          decoration: BoxDecoration(
                            color: Theme.of(context).colorScheme.primary, // Mor renk (sayfa renkleriyle uyumlu)
                            shape: BoxShape.circle,
                            boxShadow: [
                              BoxShadow(
                                color: Theme.of(context).colorScheme.primary.withOpacity(0.3),
                                blurRadius: 8,
                                spreadRadius: 1,
                                offset: const Offset(0, 2),
                              ),
                            ],
                          ),
                          child: Icon(
                            Icons.add,
                            color: Colors.white,
                            size: widget.isSmallPhone ? 18 : widget.isMobile ? 20 : 22,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
                // Favorite Button - Sağ üst köşe (Animasyonlu kırmızı)
                Positioned(
                  top: 6,
                  right: 6,
                  child: ScaleTransition(
                    scale: _favoriteScaleAnimation,
                    child: Material(
                      color: Colors.transparent,
                      child: InkWell(
                        onTap: _handleFavorite,
                        borderRadius: BorderRadius.circular(50),
                        customBorder: const CircleBorder(),
                        child: Container(
                          width: widget.isSmallPhone ? 32 : widget.isMobile ? 36 : 40,
                          height: widget.isSmallPhone ? 32 : widget.isMobile ? 36 : 40,
                          decoration: BoxDecoration(
                            color: Theme.of(context).colorScheme.surface.withOpacity(0.95),
                            shape: BoxShape.circle,
                            boxShadow: [
                              BoxShadow(
                                color: Colors.black.withOpacity(0.1),
                                blurRadius: 4,
                                offset: const Offset(0, 2),
                              ),
                            ],
                          ),
                          child: Icon(
                            _isFavorite ? Icons.favorite : Icons.favorite_border,
                            size: widget.isSmallPhone ? 18 : widget.isMobile ? 20 : 22,
                            color: _isFavorite ? Colors.red : Theme.of(context).colorScheme.onSurface.withOpacity(0.7),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),

        // Product Info - Kompakt ve modern tasarım
        Expanded(
          child: Padding(
            padding: EdgeInsets.fromLTRB(
              widget.isSmallPhone ? 8 : widget.isMobile ? 10 : 12,  // Sol
              widget.isSmallPhone ? 8 : widget.isMobile ? 10 : 12,  // Üst
              widget.isSmallPhone ? 8 : widget.isMobile ? 10 : 12,  // Sağ
              widget.isSmallPhone ? 3 : widget.isMobile ? 4 : 5,    // Alt - Maksimum minimize
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                // Product Name
                Text(
                  widget.product.name,
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                    fontSize: widget.isSmallPhone ? 13 : widget.isMobile ? 14 : 15,
                    fontWeight: FontWeight.w600,
                    height: 1.2,
                    letterSpacing: -0.2,
                    fontFamily: 'Roboto',
                  ),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
                
                // Spacer - İsim ile fiyat arasında küçük boşluk
                SizedBox(height: widget.isSmallPhone ? 6 : widget.isMobile ? 8 : 10),
                
                // Price - Büyük ve belirgin
                Text(
                  PriceFormatter.format(widget.product.discountPrice ?? widget.product.price),
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: Theme.of(context).colorScheme.primary,
                    fontWeight: FontWeight.w800,
                    fontSize: widget.isSmallPhone ? 17 : widget.isMobile ? 18 : 20,
                    letterSpacing: -0.5,
                    height: 1.0,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                
                // Rating ve Review Count - Fiyatın hemen altında, maksimum kompakt
                // Sadece rating > 0 ise göster
                if ((widget.product.rating ?? 0.0) > 0 || (widget.product.reviewCount ?? 0) > 0) ...[
                  SizedBox(height: widget.isSmallPhone ? 3 : widget.isMobile ? 4 : 5),
                  Row(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.star_rounded,
                        size: widget.isSmallPhone ? 14 : widget.isMobile ? 15 : 16,
                        color: Colors.amber[700],
                      ),
                      SizedBox(width: 3),
                      Text(
                        (widget.product.rating ?? 0.0).toStringAsFixed(1),
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          fontSize: widget.isSmallPhone ? 11 : widget.isMobile ? 12 : 13,
                          fontWeight: FontWeight.w600,
                          color: Theme.of(context).colorScheme.onSurface,
                          height: 1.0,
                          letterSpacing: -0.2,
                        ),
                      ),
                      if ((widget.product.reviewCount ?? 0) > 0) ...[
                        SizedBox(width: 4),
                        Flexible(
                          child: Text(
                            '(${widget.product.reviewCount} reviews)',
                            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                              fontSize: widget.isSmallPhone ? 10 : widget.isMobile ? 11 : 12,
                              color: Theme.of(context).colorScheme.onSurface.withOpacity(0.65),
                              fontWeight: FontWeight.w400,
                              height: 1.0,
                              letterSpacing: -0.1,
                            ),
                            overflow: TextOverflow.ellipsis,
                            maxLines: 1,
                          ),
                        ),
                      ],
                    ],
                  ),
                ],
              ],
            ),
          ),
        ),
      ],
    );
  }
}

