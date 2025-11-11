import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';

import '../../../../core/models/product_model.dart';
import '../../../../core/utils/price_formatter.dart';

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
      horizontalPadding = 12.0; // Yanlara nefes alacak boşluk
      gridSpacing = 8.0; // Ürünler arası boşluk
      childAspectRatio = 0.78; // Daha kompakt, daha fazla ürün görünsün
    } else if (isMobile) {
      // Normal mobil (400-600px)
      horizontalPadding = 16.0;
      gridSpacing = 10.0;
      childAspectRatio = 0.85; // Daha kompakt, daha fazla ürün görünsün
    } else {
      // Tablet/Desktop
      horizontalPadding = 20.0;
      gridSpacing = 16.0;
      childAspectRatio = 0.92; // Daha kompakt, daha fazla ürün görünsün
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

class _ProductCard extends StatelessWidget {
  final Product product;
  final bool isMobile;
  final bool isSmallPhone;

  const _ProductCard({
    required this.product,
    this.isMobile = false,
    this.isSmallPhone = false,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => context.push('/product/${product.id}'),
      child: Container(
        decoration: BoxDecoration(
          color: Theme.of(context).cardColor,
          borderRadius: BorderRadius.circular(isSmallPhone ? 10 : isMobile ? 12 : 16),
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
        child: _buildCompactCard(context),
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
          aspectRatio: isSmallPhone ? 1.3 : isMobile ? 1.4 : 1.5, // Daha küçük resim oranı
          child: ClipRRect(
            borderRadius: BorderRadius.vertical(
              top: Radius.circular(isSmallPhone ? 10 : isMobile ? 12 : 16),
            ),
            child: Stack(
              children: [
                CachedNetworkImage(
                  imageUrl: product.imageUrl ?? 'https://via.placeholder.com/200',
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
                // Favorite Button
                Positioned(
                  top: 4,
                  right: 4,
                  child: Container(
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.surface.withOpacity(0.9),
                      shape: BoxShape.circle,
                    ),
                    child: IconButton(
                      icon: Icon(
                        Icons.favorite_border,
                        size: isSmallPhone ? 10 : isMobile ? 12 : 14,
                        color: Theme.of(context).colorScheme.onSurface,
                      ),
                      onPressed: () {},
                      padding: EdgeInsets.zero,
                      constraints: BoxConstraints(
                        minWidth: isSmallPhone ? 18 : isMobile ? 20 : 24,
                        minHeight: isSmallPhone ? 18 : isMobile ? 20 : 24,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),

        // Product Info - Kompakt padding, daha fazla ürün görünsün
        Padding(
          padding: EdgeInsets.fromLTRB(
            isSmallPhone ? 8 : isMobile ? 10 : 12, // Sol padding optimize edildi
            isSmallPhone ? 4 : isMobile ? 5 : 6, // Üst padding azaltıldı
            isSmallPhone ? 6 : isMobile ? 8 : 10,
            isSmallPhone ? 4 : isMobile ? 5 : 6, // Alt padding azaltıldı
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min, // Minimum alan kullan
            children: [
              // Category Badge - Daha belirgin
              if (product.category != null)
                Padding(
                  padding: EdgeInsets.only(bottom: isSmallPhone ? 2 : isMobile ? 3 : 4), // Boşluk azaltıldı
                  child: Container(
                    padding: EdgeInsets.symmetric(
                      horizontal: isSmallPhone ? 5 : isMobile ? 6 : 7,
                      vertical: isSmallPhone ? 2 : 3,
                    ),
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.primary.withOpacity(0.15),
                      borderRadius: BorderRadius.circular(4),
                      border: Border.all(
                        color: Theme.of(context).colorScheme.primary.withOpacity(0.3),
                        width: 1,
                      ),
                    ),
                    child: Text(
                      product.category!.length > (isSmallPhone ? 4 : isMobile ? 5 : 6)
                          ? product.category!.substring(0, isSmallPhone ? 4 : isMobile ? 5 : 6) + '...'
                          : product.category!,
                      style: Theme.of(context).textTheme.labelSmall?.copyWith(
                        color: Theme.of(context).colorScheme.primary,
                        fontSize: isSmallPhone ? 8 : isMobile ? 9 : 10,
                        fontWeight: FontWeight.w600, // Daha kalın
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ),
              // Product Name - Kalın başlık
              Text(
                product.name,
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  fontSize: isSmallPhone ? 11 : isMobile ? 12 : 13,
                  fontWeight: FontWeight.bold, // Bold yapıldı
                  height: 1.2,
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
              // Description - Normal font, belirgin ayrım
              if (product.description != null && product.description!.isNotEmpty) ...[
                SizedBox(height: isSmallPhone ? 2 : isMobile ? 3 : 4), // Boşluk azaltıldı
                Text(
                  product.description!,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    fontSize: isSmallPhone ? 9 : isMobile ? 10 : 11,
                    color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6), // Daha açık - ayrım için
                    fontWeight: FontWeight.normal, // Normal font - başlıktan ayrım
                    height: 1.3,
                  ),
                  maxLines: isSmallPhone ? 1 : 2, // Küçük ekranlarda 1 satır, diğerlerinde 2
                  overflow: TextOverflow.ellipsis,
                ),
              ],
              // Price and Add Button - Kompakt düzen, kartın altına yakın
              SizedBox(height: isSmallPhone ? 4 : isMobile ? 5 : 6), // Boşluk azaltıldı - kartın altına yakın
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  // Price - Daha belirgin renk
                  Flexible(
                    child: Text(
                      PriceFormatter.format(product.discountPrice ?? product.price),
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: const Color(0xFF2196F3), // Açık mavi - daha belirgin
                        fontWeight: FontWeight.bold,
                        fontSize: isSmallPhone ? 13 : isMobile ? 14 : 16,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  SizedBox(width: isSmallPhone ? 8 : isMobile ? 10 : 12), // Daha fazla boşluk
                  // Add Button - 32x32 görsel, 44x44 touch target (padding ile)
                  Material(
                    color: Colors.transparent,
                    child: InkWell(
                      onTap: () {},
                      borderRadius: BorderRadius.circular(isSmallPhone ? 8 : isMobile ? 9 : 10),
                      child: Container(
                        width: 44, // Touch target 44x44 dp
                        height: 44,
                        alignment: Alignment.center,
                        child: Container(
                          width: 32, // Görsel boyut 32x32
                          height: 32,
                          decoration: BoxDecoration(
                            color: Theme.of(context).colorScheme.primary,
                            borderRadius: BorderRadius.circular(isSmallPhone ? 6 : isMobile ? 7 : 8),
                            boxShadow: [
                              BoxShadow(
                                color: Theme.of(context).colorScheme.primary.withOpacity(0.3),
                                blurRadius: 4,
                                offset: const Offset(0, 2),
                              ),
                            ],
                          ),
                          alignment: Alignment.center,
                          child: Icon(
                            Icons.add,
                            color: Colors.white,
                            size: isSmallPhone ? 16 : isMobile ? 18 : 20, // Daha küçük ikon
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }
}

