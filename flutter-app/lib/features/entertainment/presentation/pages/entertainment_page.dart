import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/models/venue_model.dart';
import '../../../../core/services/api_service.dart';
import '../../../../core/widgets/location_filter_dialog.dart';
import '../../../../core/widgets/location_map_widget.dart';
import 'package:latlong2/latlong.dart';

class EntertainmentPage extends StatefulWidget {
  const EntertainmentPage({super.key});

  @override
  State<EntertainmentPage> createState() => _EntertainmentPageState();
}

class _EntertainmentPageState extends State<EntertainmentPage> {
  List<VenueModel> _venues = [];
  bool _isLoading = true;
  String? _error;
  final ApiService _apiService = ApiService();
  String _selectedVenueType = 'All';
  final TextEditingController _searchController = TextEditingController();

  final List<String> _venueTypes = ['All', 'RESTAURANT', 'CAFE', 'BAR', 'CLUB', 'THEATER', 'CINEMA', 'SPORTS', 'OTHER'];
  
  // Location filter state
  bool _useLocationFilter = false;
  double? _filterLatitude;
  double? _filterLongitude;
  double _filterRadiusKm = 10.0;

  @override
  void initState() {
    super.initState();
    _loadVenues();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadVenues({String? venueType, String? search}) async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      List<VenueModel> fetchedVenues;
      
      if (_useLocationFilter && _filterLatitude != null && _filterLongitude != null) {
        // Use location-based search
        fetchedVenues = await _apiService.getNearbyVenues(
          _filterLatitude!,
          _filterLongitude!,
          _filterRadiusKm,
        );
        // Apply venue type and search filters locally if needed
        if (venueType != null && venueType != 'All') {
          fetchedVenues = fetchedVenues.where((venue) => venue.venueType == venueType).toList();
        }
        if (search != null && search.isNotEmpty) {
          fetchedVenues = fetchedVenues.where((venue) => 
            venue.name.toLowerCase().contains(search.toLowerCase()) ||
            (venue.description?.toLowerCase().contains(search.toLowerCase()) ?? false)
          ).toList();
        }
      } else {
        // Use regular search
        fetchedVenues = await _apiService.getVenues(
          venueType: venueType == 'All' ? null : venueType,
          search: search,
        );
      }
      
      setState(() {
        _venues = fetchedVenues;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to load venues: $e';
        _isLoading = false;
      });
    }
  }
  
  List<MapMarker> _createVenueMarkers() {
    return _venues
        .where((venue) => venue.latitude != null && venue.longitude != null)
        .map((venue) {
      IconData iconData;
      Color iconColor;
      switch (venue.venueType.toUpperCase()) {
        case 'RESTAURANT':
          iconData = Icons.restaurant;
          iconColor = Colors.orange;
          break;
        case 'CAFE':
          iconData = Icons.local_cafe;
          iconColor = Colors.brown;
          break;
        case 'BAR':
          iconData = Icons.local_bar;
          iconColor = Colors.amber;
          break;
        case 'CLUB':
          iconData = Icons.music_note;
          iconColor = Colors.purple;
          break;
        case 'THEATER':
        case 'CINEMA':
          iconData = Icons.movie;
          iconColor = Colors.red;
          break;
        case 'SPORTS':
          iconData = Icons.sports_soccer;
          iconColor = Colors.green;
          break;
        default:
          iconData = Icons.place;
          iconColor = Colors.blue;
      }
      
      return MapMarker(
        position: LatLng(venue.latitude!, venue.longitude!),
        title: venue.name,
        subtitle: venue.displayType,
        icon: Container(
          padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(
            color: iconColor,
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
          child: Icon(iconData, color: Colors.white, size: 24),
        ),
        onTap: () {
          _showVenueInfo(venue);
        },
      );
    }).toList();
  }

  void _showVenueInfo(VenueModel venue) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(venue.name),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Tip: ${venue.displayType}'),
            if (venue.city != null) Text('Şehir: ${venue.city}'),
            if (venue.address != null) Text('Adres: ${venue.address}'),
            if (venue.rating > 0)
              Text('Rating: ${venue.rating.toStringAsFixed(1)} ⭐'),
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

  void _showLocationOnMap(VenueModel venue) {
    if (venue.latitude == null || venue.longitude == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Bu mekanın konum bilgisi bulunmuyor.'),
          duration: Duration(seconds: 2),
        ),
      );
      return;
    }

    final marker = MapMarker(
      position: LatLng(venue.latitude!, venue.longitude!),
      title: venue.name,
      subtitle: venue.displayType,
      icon: Container(
        padding: const EdgeInsets.all(6),
        decoration: BoxDecoration(
          color: Colors.orange,
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
        child: const Icon(Icons.location_city, color: Colors.white, size: 24),
      ),
    );

    showDialog(
      context: context,
      builder: (context) => LocationFilterDialog(
        initialLatitude: venue.latitude!,
        initialLongitude: venue.longitude!,
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
    // Eğer veriler yükleniyorsa veya boşsa, önce yükle
    if (_isLoading || _venues.isEmpty) {
      await _loadVenues(venueType: _selectedVenueType);
    }
    
    if (!mounted) return;
    
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
          _loadVenues(venueType: _selectedVenueType);
        },
        markers: _createVenueMarkers(),
      ),
    );
  }
  
  void _clearLocationFilter() {
    setState(() {
      _useLocationFilter = false;
      _filterLatitude = null;
      _filterLongitude = null;
    });
    _loadVenues(venueType: _selectedVenueType);
  }

  void _onVenueTypeSelected(String venueType) {
    setState(() {
      _selectedVenueType = venueType;
    });
    _loadVenues(venueType: venueType);
  }

  void _onSearch(String query) {
    if (query.isEmpty) {
      _loadVenues(venueType: _selectedVenueType);
    } else {
      _loadVenues(venueType: _selectedVenueType == 'All' ? null : _selectedVenueType, search: query);
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
          onPressed: () => context.go('/explore'),
        ),
        title: const Text(
          'Mekanlar',
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
                  hintText: 'Search venues...',
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
              itemCount: _venueTypes.length,
              itemBuilder: (context, index) {
                final venueType = _venueTypes[index];
                final isSelected = venueType == _selectedVenueType;
                return Padding(
                  padding: EdgeInsets.only(
                    right: index == _venueTypes.length - 1 ? 0 : 12,
                  ),
                  child: FilterChip(
                    label: Text(venueType == 'All' ? 'All Types' : venueType.replaceAll('_', ' ')),
                    selected: isSelected,
                    onSelected: (_) => _onVenueTypeSelected(venueType),
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

          // Venue List
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
                              onPressed: () => _loadVenues(venueType: _selectedVenueType),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(0xFF8E24AA),
                                foregroundColor: Colors.white,
                              ),
                              child: const Text('Retry'),
                            ),
                          ],
                        ),
                      )
                    : _venues.isEmpty
                        ? const Center(
                            child: Text(
                              'No venues found',
                              style: TextStyle(color: Color(0xFF757575)),
                            ),
                          )
                        : RefreshIndicator(
                            onRefresh: () => _loadVenues(venueType: _selectedVenueType),
                            color: const Color(0xFF8E24AA),
                            child: ListView.builder(
                              padding: const EdgeInsets.symmetric(horizontal: 16),
                              itemCount: _venues.length,
                              itemBuilder: (context, index) {
                                return _VenueCard(
                                  venue: _venues[index],
                                  onMapTap: () => _showLocationOnMap(_venues[index]),
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

class _VenueCard extends StatelessWidget {
  final VenueModel venue;
  final VoidCallback? onMapTap;

  const _VenueCard({required this.venue, this.onMapTap});

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
                  decoration: BoxDecoration(
                    color: const Color(0xFFE0E0E0),
                    borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
                  ),
                  child: venue.imageUrl != null && venue.imageUrl!.isNotEmpty
                      ? CachedNetworkImage(
                          imageUrl: venue.imageUrl!,
                          fit: BoxFit.cover,
                          placeholder: (context, url) => Container(
                            color: const Color(0xFFE0E0E0),
                            child: const Center(
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                color: Color(0xFF8E24AA),
                              ),
                            ),
                          ),
                          errorWidget: (context, url, error) => Container(
                            color: const Color(0xFFE0E0E0),
                            child: const Icon(Icons.location_city, size: 60, color: Color(0xFF9E9E9E)),
                          ),
                        )
                      : Container(
                          color: const Color(0xFFE0E0E0),
                          child: const Icon(Icons.location_city, size: 60, color: Color(0xFF9E9E9E)),
                        ),
                ),
              ),
              // Map Location Button (Top Left)
              if (venue.latitude != null && venue.longitude != null)
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
            ],
          ),

          // Info Section
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Venue Name with Verified Badge
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        venue.name,
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
                    if (venue.isActive ?? false)
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

                const SizedBox(height: 8),

                // Description
                if (venue.description != null && venue.description!.isNotEmpty)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 8),
                    child: Text(
                      venue.description!,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        fontSize: 14,
                        color: Color(0xFF757575),
                      ),
                    ),
                  ),

                const SizedBox(height: 8),

                // Metadata Row
                Row(
                  children: [
                    // Rating
                    Row(
                      children: [
                        const Icon(Icons.star, size: 16, color: Color(0xFFFFA726)),
                        const SizedBox(width: 4),
                        Text(
                          '${venue.rating.toStringAsFixed(1)}',
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
                          venue.city ?? 'Location not specified',
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

                // Venue Type Tag
                Align(
                  alignment: Alignment.centerRight,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: const Color(0xFF8E24AA).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      venue.displayType,
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
