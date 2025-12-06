import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../../../core/models/venue_model.dart';
import '../../../../core/services/api_service.dart';

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
      final fetchedVenues = await _apiService.getVenues(
        venueType: venueType == 'All' ? null : venueType,
        search: search,
      );
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
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: const Text(
          'Mekanlar',
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
                                return _VenueCard(venue: _venues[index]);
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

  const _VenueCard({required this.venue});

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
