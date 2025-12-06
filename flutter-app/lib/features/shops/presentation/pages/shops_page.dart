import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../../../core/models/shop_model.dart';
import '../../../../core/services/api_service.dart';

class ShopsPage extends StatefulWidget {
  const ShopsPage({super.key});

  @override
  State<ShopsPage> createState() => _ShopsPageState();
}

class _ShopsPageState extends State<ShopsPage> {
  List<ShopModel> _shops = [];
  bool _isLoading = true;
  String? _error;
  final ApiService _apiService = ApiService();
  String _selectedCategory = 'All';
  final TextEditingController _searchController = TextEditingController();

  final List<String> _categories = ['All', 'Electronics', 'Fashion', 'Food', 'Home'];

  @override
  void initState() {
    super.initState();
    _loadShops();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadShops({String? category, String? search}) async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final fetchedShops = await _apiService.getShops(
        category: category == 'All' ? null : category,
        search: search,
      );
      setState(() {
        _shops = fetchedShops;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to load shops: $e';
        _isLoading = false;
      });
    }
  }

  void _onCategorySelected(String category) {
    setState(() {
      _selectedCategory = category;
    });
    _loadShops(category: category);
  }

  void _onSearch(String query) {
    if (query.isEmpty) {
      _loadShops(category: _selectedCategory);
    } else {
      _loadShops(category: _selectedCategory == 'All' ? null : _selectedCategory, search: query);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Color(0xFF1A1A1A)),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: const Text(
          'Shops',
          style: TextStyle(
            color: Color(0xFF1A1A1A),
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
      body: Column(
        children: [
          // Search Bar
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12.0),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: TextField(
                controller: _searchController,
                decoration: InputDecoration(
                  hintText: 'Search shops...',
                  hintStyle: const TextStyle(color: Color(0xFF757575)),
                  prefixIcon: const Icon(Icons.search, color: Color(0xFF757575)),
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                ),
                onChanged: _onSearch,
              ),
            ),
          ),

          // Filter Bar - Horizontal Scrollable
          SizedBox(
            height: 50,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _categories.length,
              itemBuilder: (context, index) {
                final category = _categories[index];
                final isSelected = category == _selectedCategory;
                return Padding(
                  padding: EdgeInsets.only(
                    right: index == _categories.length - 1 ? 0 : 12,
                  ),
                  child: FilterChip(
                    label: Text(category),
                    selected: isSelected,
                    onSelected: (_) => _onCategorySelected(category),
                    backgroundColor: Colors.white,
                    selectedColor: const Color(0xFF8E24AA),
                    labelStyle: TextStyle(
                      color: isSelected ? Colors.white : const Color(0xFF1A1A1A),
                      fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
                    ),
                    side: BorderSide(
                      color: isSelected ? const Color(0xFF8E24AA) : const Color(0xFFE0E0E0),
                      width: 1,
                    ),
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20),
                    ),
                  ),
                );
              },
            ),
          ),

          const SizedBox(height: 16),

          // Shop List
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator(color: Color(0xFF8E24AA)))
                : _error != null
                    ? Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const Icon(Icons.error_outline, size: 64, color: Colors.red),
                            const SizedBox(height: 16),
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 32),
                              child: Text(
                                _error!,
                                textAlign: TextAlign.center,
                                style: const TextStyle(color: Colors.red),
                              ),
                            ),
                            const SizedBox(height: 16),
                            ElevatedButton(
                              onPressed: () => _loadShops(category: _selectedCategory),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(0xFF8E24AA),
                                foregroundColor: Colors.white,
                              ),
                              child: const Text('Retry'),
                            ),
                          ],
                        ),
                      )
                    : _shops.isEmpty
                        ? const Center(
                            child: Text(
                              'No shops found',
                              style: TextStyle(color: Color(0xFF757575)),
                            ),
                          )
                        : RefreshIndicator(
                            onRefresh: () => _loadShops(category: _selectedCategory),
                            color: const Color(0xFF8E24AA),
                            child: ListView.builder(
                              padding: const EdgeInsets.symmetric(horizontal: 16),
                              itemCount: _shops.length,
                              itemBuilder: (context, index) {
                                return _ShopCard(shop: _shops[index]);
                              },
                            ),
                          ),
          ),
        ],
      ),
    );
  }
}

class _ShopCard extends StatelessWidget {
  final ShopModel shop;

  const _ShopCard({required this.shop});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Cover Image Section
          Stack(
            children: [
              ClipRRect(
                borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
                child: Container(
                  height: 160,
                  width: double.infinity,
                  color: const Color(0xFFE0E0E0),
                  child: shop.displayImage.isNotEmpty
                      ? CachedNetworkImage(
                          imageUrl: shop.displayImage,
                          fit: BoxFit.cover,
                          placeholder: (context, url) => Container(
                            color: const Color(0xFFE0E0E0),
                            child: const Center(
                              child: CircularProgressIndicator(strokeWidth: 2),
                            ),
                          ),
                          errorWidget: (context, url, error) => Container(
                            color: const Color(0xFFE0E0E0),
                            child: const Icon(Icons.store, size: 60, color: Color(0xFF9E9E9E)),
                          ),
                        )
                      : const Icon(Icons.store, size: 60, color: Color(0xFF9E9E9E)),
                ),
              ),
              // Favorite Icon (Top Right)
              Positioned(
                top: 12,
                right: 12,
                child: Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.9),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(
                    Icons.favorite_border,
                    size: 20,
                    color: Color(0xFF757575),
                  ),
                ),
              ),
              // Shop Logo (Bottom Left Overlap)
              Positioned(
                bottom: -30,
                left: 16,
                child: Container(
                  width: 60,
                  height: 60,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    shape: BoxShape.circle,
                    border: Border.all(color: Colors.white, width: 2),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.1),
                        blurRadius: 8,
                        offset: const Offset(0, 2),
                      ),
                    ],
                  ),
                  child: shop.displayImage.isNotEmpty
                      ? ClipOval(
                          child: CachedNetworkImage(
                            imageUrl: shop.displayImage,
                            fit: BoxFit.cover,
                            placeholder: (context, url) => Container(
                              color: const Color(0xFFE0E0E0),
                              child: const Center(
                                child: CircularProgressIndicator(strokeWidth: 2),
                              ),
                            ),
                            errorWidget: (context, url, error) => Container(
                              color: const Color(0xFFE0E0E0),
                              child: const Icon(Icons.store, size: 30, color: Color(0xFF9E9E9E)),
                            ),
                          ),
                        )
                      : Container(
                          color: const Color(0xFFE0E0E0),
                          child: const Icon(Icons.store, size: 30, color: Color(0xFF9E9E9E)),
                        ),
                ),
              ),
            ],
          ),

          // Info Section
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 40, 16, 12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Shop Name with Verified Badge
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        shop.name,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: Color(0xFF1A1A1A),
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.all(4),
                      decoration: const BoxDecoration(
                        color: Colors.blue,
                        shape: BoxShape.circle,
                      ),
                      child: const Icon(
                        Icons.check,
                        size: 16,
                        color: Colors.white,
                      ),
                    ),
                  ],
                ),

                const SizedBox(height: 12),

                // Metadata Row
                Row(
                  children: [
                    // Rating
                    Row(
                      children: [
                        const Icon(Icons.star, size: 16, color: Color(0xFFFFA726)),
                        const SizedBox(width: 4),
                        Text(
                          '${shop.rating.toStringAsFixed(1)}',
                          style: const TextStyle(
                            fontSize: 12,
                            color: Color(0xFF757575),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(width: 16),
                    // Location
                    Row(
                      children: [
                        const Icon(Icons.location_on, size: 16, color: Color(0xFF757575)),
                        const SizedBox(width: 4),
                        Text(
                          shop.city ?? 'Location not specified',
                          style: const TextStyle(
                            fontSize: 12,
                            color: Color(0xFF757575),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),

                const SizedBox(height: 12),

                // Category Tag
                Align(
                  alignment: Alignment.centerRight,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: const Color(0xFF8E24AA).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      shop.category,
                      style: const TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                        color: Color(0xFF8E24AA),
                      ),
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
}
