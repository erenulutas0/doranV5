import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../../../core/models/product_model.dart';
import '../../../../core/providers/product_provider.dart';

class ProductsPage extends StatefulWidget {
  final String? from;
  const ProductsPage({super.key, this.from});

  @override
  State<ProductsPage> createState() => _ProductsPageState();
}

class _ProductsPageState extends State<ProductsPage> {
  static const Color _neonGreen = Color(0xFFCCFF00);
  static const Color _darkBg = Color(0xFF0B0B0B);
  static const Color _cardBg = Color(0xFF1E1E1E);

  String _selectedCategory = 'All';
  final TextEditingController _searchController = TextEditingController();
  String _sortBy = 'newest'; // newest, price-low, price-high, rating

  final List<String> _categories = [
    'All',
    'Electronics',
    'Clothing',
    'Home & Garden',
    'Sports',
    'Books',
    'Toys',
  ];

  @override
  void initState() {
    super.initState();
    // Load products using ProductProvider
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final productProvider = context.read<ProductProvider>();
      // Always try to fetch if empty, but don't block if there's cached data
      if (productProvider.products.isEmpty && !productProvider.isLoading) {
        productProvider.fetchProducts().catchError((error) {
          // Silently handle error, will be shown in UI via Consumer
          if (mounted) {
            // Retry after a short delay if error occurs
            Future.delayed(const Duration(seconds: 2), () {
              if (mounted && productProvider.products.isEmpty) {
                productProvider.fetchProducts();
              }
            });
          }
        });
      }
    });
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  List<Product> _getFilteredProducts(List<Product> products) {
    List<Product> filtered = List.from(products);
    
    // Apply category filter
    if (_selectedCategory != 'All') {
      filtered = filtered.where((p) => p.category == _selectedCategory).toList();
    }
    
    // Apply search filter
    if (_searchController.text.isNotEmpty) {
      final search = _searchController.text.toLowerCase();
      filtered = filtered.where((p) => 
        p.name.toLowerCase().contains(search) ||
        (p.description?.toLowerCase().contains(search) ?? false) ||
        (p.category?.toLowerCase().contains(search) ?? false)
      ).toList();
    }
    
    // Apply sorting
    switch (_sortBy) {
      case 'price-low':
        filtered.sort((a, b) => a.price.compareTo(b.price));
        break;
      case 'price-high':
        filtered.sort((a, b) => b.price.compareTo(a.price));
        break;
      case 'rating':
        filtered.sort((a, b) {
          final aRating = a.rating ?? 0;
          final bRating = b.rating ?? 0;
          return bRating.compareTo(aRating);
        });
        break;
      case 'newest':
      default:
        // Keep original order (newest first from API)
        break;
    }
    
    return filtered;
  }

  void _onCategoryChanged(String category) {
    setState(() {
      _selectedCategory = category;
    });
  }

  void _onSearchChanged(String value) {
    setState(() {});
  }

  void _onSortChanged(String? value) {
    if (value != null) {
      setState(() {
        _sortBy = value;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: _darkBg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            if (context.canPop()) {
              context.pop();
            } else {
              final from = widget.from;
              if (from == 'home') {
                context.go('/home');
              } else if (from == 'explore') {
                context.go('/explore');
              } else {
                context.go('/home');
              }
            }
          },
        ),
        title: const Text(
          'Products',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.sort, color: Colors.white),
            onPressed: () {
              showModalBottomSheet(
                context: context,
                backgroundColor: _cardBg,
                shape: const RoundedRectangleBorder(
                  borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
                ),
                builder: (context) => Container(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Sort By',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 16),
                      _buildSortOption('newest', 'Newest First', Icons.access_time),
                      _buildSortOption('price-low', 'Price: Low to High', Icons.arrow_upward),
                      _buildSortOption('price-high', 'Price: High to Low', Icons.arrow_downward),
                      _buildSortOption('rating', 'Highest Rated', Icons.star),
                    ],
                  ),
                ),
              );
            },
          ),
        ],
      ),
      body: Column(
        children: [
          // Search Bar
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              controller: _searchController,
              onChanged: _onSearchChanged,
              style: const TextStyle(color: Colors.white),
              decoration: InputDecoration(
                hintText: 'Search products...',
                hintStyle: TextStyle(color: Colors.white.withOpacity(0.5)),
                prefixIcon: const Icon(Icons.search, color: Colors.white70),
                filled: true,
                fillColor: _cardBg,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: _neonGreen.withOpacity(0.3)),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: _neonGreen.withOpacity(0.3)),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: const BorderSide(color: _neonGreen, width: 2),
                ),
              ),
            ),
          ),

          // Category Chips
          SizedBox(
            height: 50,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _categories.length,
              itemBuilder: (context, index) {
                final category = _categories[index];
                final isSelected = _selectedCategory == category;
                return Padding(
                  padding: const EdgeInsets.only(right: 8),
                  child: FilterChip(
                    label: Text(category),
                    selected: isSelected,
                    onSelected: (selected) => _onCategoryChanged(category),
                    selectedColor: _neonGreen,
                    backgroundColor: _cardBg,
                    labelStyle: TextStyle(
                      color: isSelected ? Colors.black : Colors.white,
                      fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                    ),
                    side: BorderSide(
                      color: isSelected ? _neonGreen : Colors.white24,
                      width: 1.2,
                    ),
                  ),
                );
              },
            ),
          ),

          const SizedBox(height: 8),

          // Products List
          Expanded(
            child: Consumer<ProductProvider>(
              builder: (context, productProvider, child) {
                if (productProvider.isLoading) {
                  return const Center(
                    child: CircularProgressIndicator(
                      color: _neonGreen,
                    ),
                  );
                }

                if (productProvider.error != null) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.error_outline,
                          size: 64,
                          color: Colors.red.withOpacity(0.7),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          productProvider.error!,
                          style: const TextStyle(color: Colors.white70),
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 16),
                        ElevatedButton(
                          onPressed: () => productProvider.fetchProducts(),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: _neonGreen,
                            foregroundColor: Colors.black,
                          ),
                          child: const Text('Retry'),
                        ),
                      ],
                    ),
                  );
                }

                final filteredProducts = _getFilteredProducts(productProvider.products);

                if (filteredProducts.isEmpty) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.inventory_2_outlined,
                          size: 64,
                          color: Colors.white.withOpacity(0.3),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'No products found',
                          style: TextStyle(
                            color: Colors.white.withOpacity(0.6),
                            fontSize: 16,
                          ),
                        ),
                      ],
                    ),
                  );
                }

                return GridView.builder(
                  padding: const EdgeInsets.all(16),
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 12,
                    mainAxisSpacing: 12,
                    childAspectRatio: 0.62,
                  ),
                  itemCount: filteredProducts.length,
                  itemBuilder: (context, index) {
                    final product = filteredProducts[index];
                    return _ProductCard(product: product);
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSortOption(String value, String label, IconData icon) {
    final isSelected = _sortBy == value;
    return InkWell(
      onTap: () {
        _onSortChanged(value);
        Navigator.pop(context);
      },
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
        margin: const EdgeInsets.only(bottom: 8),
        decoration: BoxDecoration(
          color: isSelected ? _neonGreen.withOpacity(0.2) : Colors.transparent,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(
            color: isSelected ? _neonGreen : Colors.white24,
            width: 1,
          ),
        ),
        child: Row(
          children: [
            Icon(
              icon,
                      color: isSelected ? _neonGreen : Colors.white70,
              size: 20,
            ),
            const SizedBox(width: 12),
            Text(
              label,
              style: TextStyle(
                      color: isSelected ? _neonGreen : Colors.white,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
              ),
            ),
            const Spacer(),
            if (isSelected)
              const Icon(
                Icons.check,
                color: _neonGreen,
                size: 20,
              ),
          ],
        ),
      ),
    );
  }
}

class _ProductCard extends StatelessWidget {
  static const Color _neonGreen = Color(0xFFCCFF00);
  static const Color _cardBg = Color(0xFF1E1E1E);

  final Product product;
  const _ProductCard({required this.product});

  @override
  Widget build(BuildContext context) {

    return InkWell(
      onTap: () {
        context.push('/product/${product.id}');
      },
      borderRadius: BorderRadius.circular(12),
      child: Container(
        decoration: BoxDecoration(
          color: _cardBg,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.white12, width: 1),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Product Image
            ClipRRect(
              borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
              child: AspectRatio(
                aspectRatio: 1,
                child: product.imageUrl != null && product.imageUrl!.isNotEmpty
                    ? CachedNetworkImage(
                        imageUrl: product.imageUrl!,
                        fit: BoxFit.cover,
                        placeholder: (context, url) => Container(
                          color: Colors.grey[900],
                          child: const Center(
                            child: CircularProgressIndicator(
                              color: _neonGreen,
                              strokeWidth: 2,
                            ),
                          ),
                        ),
                        errorWidget: (context, url, error) => Container(
                          color: Colors.grey[900],
                          child: const Icon(
                            Icons.image_not_supported,
                            color: Colors.white38,
                            size: 40,
                          ),
                        ),
                      )
                    : Container(
                        color: Colors.grey[900],
                        child: const Icon(
                          Icons.image_not_supported,
                          color: Colors.white38,
                          size: 40,
                        ),
                      ),
              ),
            ),

            // Product Info
            Flexible(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Category Badge
                    if (product.category != null)
                      Padding(
                        padding: const EdgeInsets.only(bottom: 3),
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 1.5),
                          decoration: BoxDecoration(
                            color: _neonGreen.withOpacity(0.2),
                            borderRadius: BorderRadius.circular(3),
                            border: Border.all(color: _neonGreen.withOpacity(0.5)),
                          ),
                          child: Text(
                            product.category!,
                            style: const TextStyle(
                              color: _neonGreen,
                              fontSize: 8,
                              fontWeight: FontWeight.w600,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                      ),

                    // Product Name
                    Text(
                      product.name,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                        height: 1.15,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),

                    // Price Row
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Flexible(
                          child: Text(
                            '\$${product.price.toStringAsFixed(2)}',
                            style: const TextStyle(
                              color: _neonGreen,
                              fontSize: 14,
                              fontWeight: FontWeight.bold,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        if (product.discountPrice != null)
                          Padding(
                            padding: const EdgeInsets.only(left: 4),
                            child: Text(
                              '\$${product.discountPrice!.toStringAsFixed(2)}',
                              style: TextStyle(
                                color: Colors.white.withOpacity(0.5),
                                fontSize: 10,
                                decoration: TextDecoration.lineThrough,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                      ],
                    ),

                    const SizedBox(height: 3),

                    // Rating and Review Count
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        // Rating with stars and review count
                        if (product.rating != null && product.rating! > 0)
                          Flexible(
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                const Icon(
                                  Icons.star,
                                  color: Colors.amber,
                                  size: 11,
                                ),
                                const SizedBox(width: 2),
                                Flexible(
                                  child: Text(
                                    product.rating!.toStringAsFixed(1),
                                    style: const TextStyle(
                                      color: Colors.white70,
                                      fontSize: 10,
                                      fontWeight: FontWeight.w500,
                                    ),
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                  ),
                                ),
                                if (product.reviewCount != null && product.reviewCount! > 0) ...[
                                  const SizedBox(width: 3),
                                  Flexible(
                                    child: Text(
                                      '(${product.reviewCount})',
                                      style: TextStyle(
                                        color: Colors.white.withOpacity(0.5),
                                        fontSize: 9,
                                      ),
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                  ),
                                ],
                              ],
                            ),
                          )
                        else
                          const SizedBox.shrink(),

                        // Stock Status
                        if (product.stockQuantity != null)
                          Flexible(
                            child: Text(
                              product.stockQuantity! > 0 ? '✓' : '✗',
                              style: TextStyle(
                                color: product.stockQuantity! > 0
                                    ? _neonGreen
                                    : Colors.red.withOpacity(0.7),
                                fontSize: 12,
                                fontWeight: FontWeight.bold,
                              ),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

