import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../../../core/models/shop_model.dart';
import '../../../../core/services/api_service.dart';
import '../../../../core/widgets/location_filter_dialog.dart';
import '../../../../core/widgets/location_map_widget.dart';
import 'package:latlong2/latlong.dart';

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
  
  // Location filter state
  bool _useLocationFilter = false;
  double? _filterLatitude;
  double? _filterLongitude;
  double _filterRadiusKm = 10.0;

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
      List<ShopModel> fetchedShops;
      
      if (_useLocationFilter && _filterLatitude != null && _filterLongitude != null) {
        // Use location-based search
        fetchedShops = await _apiService.getNearbyShops(
          _filterLatitude!,
          _filterLongitude!,
          _filterRadiusKm,
        );
        // Apply category and search filters locally if needed
        if (category != null && category != 'All') {
          fetchedShops = fetchedShops.where((shop) => shop.category == category).toList();
        }
        if (search != null && search.isNotEmpty) {
          fetchedShops = fetchedShops.where((shop) => 
            shop.name.toLowerCase().contains(search.toLowerCase()) ||
            (shop.description?.toLowerCase().contains(search.toLowerCase()) ?? false)
          ).toList();
        }
      } else {
        // Use regular search
        fetchedShops = await _apiService.getShops(
          category: category == 'All' ? null : category,
          search: search,
        );
      }
      
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
  
  List<MapMarker> _createShopMarkers() {
    debugPrint('🔍 Creating markers for ${_shops.length} shops');
    final shopsWithLocation = _shops.where((shop) {
      final hasLocation = shop.latitude != null && shop.longitude != null;
      if (!hasLocation) {
        debugPrint('⚠️ Shop "${shop.name}" has no location (lat: ${shop.latitude}, lng: ${shop.longitude})');
      }
      return hasLocation;
    }).toList();
    debugPrint('✅ Shops with location: ${shopsWithLocation.length} / ${_shops.length}');
    if (shopsWithLocation.isEmpty) {
      debugPrint('❌ No shops with location data!');
    }
    return shopsWithLocation.map((shop) {
      debugPrint('📍 Creating marker for: ${shop.name} at (${shop.latitude}, ${shop.longitude})');
      return MapMarker(
        position: LatLng(shop.latitude!, shop.longitude!),
        title: shop.name,
        subtitle: shop.category,
        icon: Container(
          padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(
            color: Colors.blue,
            shape: BoxShape.circle,
            border: Border.all(color: Colors.white, width: 3),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.5),
                blurRadius: 8,
                offset: const Offset(0, 3),
                spreadRadius: 1,
              ),
            ],
          ),
          child: const Icon(Icons.store, color: Colors.white, size: 24),
        ),
        onTap: () {
          _showShopInfo(shop);
        },
      );
    }).toList();
  }

  void _showShopInfo(ShopModel shop) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(shop.name),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Kategori: ${shop.category}'),
            if (shop.city != null) Text('Şehir: ${shop.city}'),
            if (shop.address != null) Text('Adres: ${shop.address}'),
            if (shop.rating > 0)
              Text('Rating: ${shop.rating.toStringAsFixed(1)} ⭐'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Kapat'),
          ),
        ],
      ),
    );
  }

  void _showLocationOnMap(ShopModel shop) {
    if (shop.latitude == null || shop.longitude == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Bu mekanın konum bilgisi bulunmuyor.'),
          duration: Duration(seconds: 2),
        ),
      );
      return;
    }

    final marker = MapMarker(
      position: LatLng(shop.latitude!, shop.longitude!),
      title: shop.name,
      subtitle: shop.category,
      icon: Container(
        padding: const EdgeInsets.all(6),
        decoration: BoxDecoration(
          color: Colors.blue,
          shape: BoxShape.circle,
          border: Border.all(color: Colors.white, width: 3),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.5),
              blurRadius: 8,
              offset: const Offset(0, 3),
              spreadRadius: 1,
            ),
          ],
        ),
        child: const Icon(Icons.store, color: Colors.white, size: 24),
      ),
    );

    showDialog(
      context: context,
      builder: (context) => LocationFilterDialog(
        initialLatitude: shop.latitude!,
        initialLongitude: shop.longitude!,
        initialRadiusKm: 10.0,
        onApply: (latitude, longitude, radiusKm) {
          // Sadece gösterim için, filtreleme yapmıyoruz
          Navigator.of(context).pop();
        },
        markers: [marker],
      ),
    );
  }

  void _openLocationFilter() async {
    debugPrint('🗺️ Opening location filter dialog...');
    debugPrint('📊 Current shops count: ${_shops.length}');
    debugPrint('⏳ Loading state: $_isLoading');
    
    // Eğer veriler yükleniyorsa veya boşsa, önce yükle
    if (_isLoading || _shops.isEmpty) {
      debugPrint('📥 Loading shops before opening dialog...');
      await _loadShops(category: _selectedCategory);
      debugPrint('✅ Shops loaded: ${_shops.length}');
    }
    
    if (!mounted) return;
    
    final markers = _createShopMarkers();
    debugPrint('🎯 Created ${markers.length} markers for dialog');
    
    showDialog(
      context: context,
      builder: (context) => LocationFilterDialog(
        initialLatitude: _filterLatitude,
        initialLongitude: _filterLongitude,
        initialRadiusKm: _filterRadiusKm,
        onApply: (latitude, longitude, radiusKm) {
          setState(() {
            _useLocationFilter = true;
            _filterLatitude = latitude;
            _filterLongitude = longitude;
            _filterRadiusKm = radiusKm;
          });
          _loadShops(category: _selectedCategory);
        },
        markers: markers,
      ),
    );
  }
  
  void _clearLocationFilter() {
    setState(() {
      _useLocationFilter = false;
      _filterLatitude = null;
      _filterLongitude = null;
    });
    _loadShops(category: _selectedCategory);
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
        actions: [
          // Location filter button
          IconButton(
            icon: Icon(
              _useLocationFilter ? Icons.location_on : Icons.location_off,
              color: _useLocationFilter ? const Color(0xFF8E24AA) : const Color(0xFF757575),
            ),
            onPressed: _openLocationFilter,
            tooltip: 'Konum Filtresi',
          ),
          // Clear location filter button (if active)
          if (_useLocationFilter)
            IconButton(
              icon: const Icon(Icons.clear, color: Colors.red),
              onPressed: _clearLocationFilter,
              tooltip: 'Konum Filtresini Temizle',
            ),
        ],
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
                                return _ShopCard(
                                  shop: _shops[index],
                                  onMapTap: () => _showLocationOnMap(_shops[index]),
                                );
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
  final VoidCallback? onMapTap;

  const _ShopCard({required this.shop, this.onMapTap});

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
              // Map Location Button (Top Left)
              if (shop.latitude != null && shop.longitude != null)
                Positioned(
                  top: 12,
                  left: 12,
                  child: Material(
                    color: Colors.transparent,
                    child: InkWell(
                      onTap: onMapTap,
                      borderRadius: BorderRadius.circular(20),
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.95),
                          shape: BoxShape.circle,
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withOpacity(0.15),
                              blurRadius: 8,
                              offset: const Offset(0, 2),
                            ),
                          ],
                        ),
                        child: const Icon(
                          Icons.map,
                          size: 20,
                          color: Color(0xFF8E24AA),
                        ),
                      ),
                    ),
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
