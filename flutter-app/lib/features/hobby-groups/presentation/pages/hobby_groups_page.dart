import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../../../core/models/hobby_group_model.dart';
import '../../../../core/services/api_service.dart';
import '../../../../core/widgets/location_filter_dialog.dart';
import '../../../../core/widgets/location_map_widget.dart';
import 'package:latlong2/latlong.dart';

class HobbyGroupsPage extends StatefulWidget {
  const HobbyGroupsPage({super.key});

  @override
  State<HobbyGroupsPage> createState() => _HobbyGroupsPageState();
}

class _HobbyGroupsPageState extends State<HobbyGroupsPage> {
  final ApiService _apiService = ApiService();
  List<HobbyGroupModel> _groups = [];
  bool _isLoading = true;
  String? _error;
  String? _selectedCategory;
  String? _selectedLocation;
  final TextEditingController _searchController = TextEditingController();
  final List<String> _categories = ['All', 'Sports', 'Arts', 'Music', 'Gaming', 'Reading', 'Cooking', 'Travel', 'Photography', 'Other'];
  
  // Location filter state
  bool _useLocationFilter = false;
  double? _filterLatitude;
  double? _filterLongitude;
  double _filterRadiusKm = 10.0;

  @override
  void initState() {
    super.initState();
    _loadGroups();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadGroups() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      List<HobbyGroupModel> groups;
      
      if (_useLocationFilter && _filterLatitude != null && _filterLongitude != null) {
        // Use location-based search
        groups = await _apiService.getNearbyHobbyGroups(
          _filterLatitude!,
          _filterLongitude!,
          _filterRadiusKm,
        );
        // Apply category and search filters locally if needed
        if (_selectedCategory != null && _selectedCategory != 'All') {
          groups = groups.where((group) => group.category == _selectedCategory).toList();
        }
        if (_searchController.text.isNotEmpty) {
          final search = _searchController.text.toLowerCase();
          groups = groups.where((group) => 
            group.name.toLowerCase().contains(search) ||
            (group.description?.toLowerCase().contains(search) ?? false)
          ).toList();
        }
      } else {
        // Use regular search
        groups = await _apiService.getHobbyGroups(
          category: _selectedCategory == 'All' ? null : _selectedCategory,
          location: _selectedLocation,
          search: _searchController.text.isEmpty ? null : _searchController.text,
        );
      }
      
      setState(() {
        _groups = groups;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }
  
  List<MapMarker> _createHobbyGroupMarkers() {
    if (kDebugMode) {
      debugPrint('📊 Current hobby groups count: ${_groups.length}');
      final groupsWithLocation = _groups.where((group) => group.latitude != null && group.longitude != null).toList();
      debugPrint('✅ Hobby groups with location: ${groupsWithLocation.length} / ${_groups.length}');
      debugPrint('🔍 Creating markers for ${groupsWithLocation.length} hobby groups...');
    }
    return _groups
        .where((group) => group.latitude != null && group.longitude != null)
        .map((group) {
      if (kDebugMode) {
        debugPrint('🎯 Hobby group marker: "${group.name}" at (${group.latitude}, ${group.longitude})');
      }
      return MapMarker(
        position: LatLng(group.latitude!, group.longitude!),
        title: group.name,
        subtitle: group.category,
        icon: Container(
          padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(
            color: Colors.pink,
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
          child: const Icon(Icons.group, color: Colors.white, size: 24),
        ),
        onTap: () {
          _showHobbyGroupInfo(group);
        },
      );
    }).toList();
  }

  void _showHobbyGroupInfo(HobbyGroupModel group) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(group.name),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Kategori: ${group.category}'),
            if (group.location != null) Text('Konum: ${group.location}'),
            Text('Üyeler: ${group.currentMembers}/${group.maxMembersCount > 0 ? group.maxMembersCount : "∞"}'),
            if (group.description != null) 
              Padding(
                padding: const EdgeInsets.only(top: 8),
                child: Text('Açıklama: ${group.description}'),
              ),
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

  void _openLocationFilter() async {
    // Eğer veriler yükleniyorsa veya boşsa, önce yükle
    if (_isLoading || _groups.isEmpty) {
      await _loadGroups();
    }
    
    if (!mounted) return;
    
    if (kDebugMode) {
      debugPrint('🗺️ Opening location filter dialog for Hobby Groups...');
      final markers = _createHobbyGroupMarkers();
      debugPrint('🎯 Created ${markers.length} markers for dialog');
    }
    
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
          _loadGroups();
        },
        markers: _createHobbyGroupMarkers(),
      ),
    );
  }
  
  void _clearLocationFilter() {
    setState(() {
      _useLocationFilter = false;
      _filterLatitude = null;
      _filterLongitude = null;
    });
    _loadGroups();
  }

  String _getCategoryImageUrl(String category) {
    switch (category) {
      case 'Sports':
        return 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      case 'Arts':
        return 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      case 'Music':
        return 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      case 'Gaming':
        return 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      case 'Reading':
        return 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      case 'Cooking':
        return 'https://images.unsplash.com/photo-1556910103-1c02745aae4d?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      case 'Travel':
        return 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      case 'Photography':
        return 'https://images.unsplash.com/photo-1502920917128-1aa500764cbd?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
      default:
        return 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/explore'),
        ),
        title: const Text('Hobi Grupları'),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black,
        actions: [
          // Location filter button
          IconButton(
            icon: Icon(
              _useLocationFilter ? Icons.location_on : Icons.location_off,
              color: _useLocationFilter ? Colors.purple : Colors.grey,
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
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: 'Search hobby groups...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          _loadGroups();
                        },
                      )
                    : null,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                filled: true,
                fillColor: Colors.grey[100],
              ),
              onSubmitted: (_) => _loadGroups(),
            ),
          ),

          // Category Filter
          SizedBox(
            height: 50,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: _categories.length,
              itemBuilder: (context, index) {
                final category = _categories[index];
                final isSelected = _selectedCategory == category || (_selectedCategory == null && category == 'All');
                return Padding(
                  padding: const EdgeInsets.only(right: 8),
                  child: FilterChip(
                    label: Text(category),
                    selected: isSelected,
                    onSelected: (selected) {
                      setState(() {
                        _selectedCategory = selected ? category : null;
                      });
                      _loadGroups();
                    },
                    selectedColor: Colors.purple.withOpacity(0.2),
                    checkmarkColor: Colors.purple,
                  ),
                );
              },
            ),
          ),

          const SizedBox(height: 16),

          // Groups List
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _error != null
                    ? Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(Icons.error_outline, size: 64, color: Colors.red[300]),
                            const SizedBox(height: 16),
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 32),
                              child: Text(
                                _error!,
                                textAlign: TextAlign.center,
                                style: TextStyle(color: Colors.red[700]),
                              ),
                            ),
                            const SizedBox(height: 16),
                            ElevatedButton(
                              onPressed: _loadGroups,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.purple,
                                foregroundColor: Colors.white,
                              ),
                              child: const Text('Retry'),
                            ),
                          ],
                        ),
                      )
                    : _groups.isEmpty
                        ? Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Icon(Icons.group_outlined, size: 64, color: Colors.grey[400]),
                                const SizedBox(height: 16),
                                Text(
                                  'No hobby groups found',
                                  style: TextStyle(color: Colors.grey[600], fontSize: 16),
                                ),
                              ],
                            ),
                          )
                        : RefreshIndicator(
                            onRefresh: _loadGroups,
                            child: ListView.builder(
                              padding: const EdgeInsets.all(16),
                              itemCount: _groups.length,
                              itemBuilder: (context, index) {
                                final group = _groups[index];
                                return _buildGroupCard(group);
                              },
                            ),
                          ),
          ),
        ],
      ),
    );
  }

  Widget _buildGroupCard(HobbyGroupModel group) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Stack(
        children: [
          InkWell(
            onTap: () {
              // Navigate to group detail
            },
            borderRadius: BorderRadius.circular(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Group Image
                ClipRRect(
                  borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                  child: Container(
                    height: 200,
                    width: double.infinity,
                    color: Colors.grey[300],
                    child: group.imageUrl != null && group.imageUrl!.isNotEmpty
                        ? CachedNetworkImage(
                            imageUrl: group.imageUrl!,
                            fit: BoxFit.cover,
                            placeholder: (context, url) => Container(
                              color: Colors.grey[300],
                              child: const Center(child: CircularProgressIndicator()),
                            ),
                            errorWidget: (context, url, error) => CachedNetworkImage(
                              imageUrl: _getCategoryImageUrl(group.category),
                              fit: BoxFit.cover,
                              placeholder: (context, url) => Container(
                                color: Colors.grey[300],
                                child: const Center(child: CircularProgressIndicator()),
                              ),
                              errorWidget: (context, url, error) => Container(
                                color: Colors.grey[300],
                                child: const Icon(Icons.group, size: 64, color: Colors.grey),
                              ),
                            ),
                          )
                        : CachedNetworkImage(
                            imageUrl: _getCategoryImageUrl(group.category),
                            fit: BoxFit.cover,
                            placeholder: (context, url) => Container(
                              color: Colors.grey[300],
                              child: const Center(child: CircularProgressIndicator()),
                            ),
                            errorWidget: (context, url, error) => Container(
                              color: Colors.grey[300],
                              child: const Icon(Icons.group, size: 64, color: Colors.grey),
                            ),
                          ),
                  ),
                ),

            // Group Info
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(
                        child: Text(
                          group.name,
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      if (group.isFull)
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: Colors.red.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: const Text(
                            'Full',
                            style: TextStyle(
                              color: Colors.red,
                              fontSize: 12,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  if (group.description != null && group.description!.isNotEmpty)
                    Text(
                      group.description!,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                  const SizedBox(height: 8),
                  if (group.location != null)
                    Row(
                      children: [
                        Icon(Icons.location_on, size: 16, color: Colors.grey[600]),
                        const SizedBox(width: 4),
                        Text(
                          group.location!,
                          style: TextStyle(color: Colors.grey[600], fontSize: 14),
                        ),
                      ],
                    ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Icon(Icons.people, size: 16, color: Colors.grey[600]),
                      const SizedBox(width: 4),
                      Text(
                        '${group.currentMembers}${group.maxMembersCount > 0 ? '/${group.maxMembersCount}' : ''} members',
                        style: TextStyle(color: Colors.grey[600], fontSize: 14),
                      ),
                      const Spacer(),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(
                          color: Colors.purple.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          group.category,
                          style: TextStyle(
                            color: Colors.purple[700],
                            fontSize: 12,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ),
                    ],
                  ),
                  if (group.tags != null && group.tags!.isNotEmpty) ...[
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 4,
                      runSpacing: 4,
                      children: group.tags!.take(3).map((tag) {
                        return Chip(
                          label: Text(
                            tag,
                            style: const TextStyle(fontSize: 10),
                          ),
                          padding: EdgeInsets.zero,
                          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                          visualDensity: VisualDensity.compact,
                        );
                      }).toList(),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
          // Map Location Button (Top Right)
          if (group.latitude != null && group.longitude != null)
            Positioned(
              top: 12,
              right: 12,
              child: Material(
                color: Colors.transparent,
                child: InkWell(
                  onTap: () {
                    _showLocationOnMap(group);
                  },
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
        ],
      ),
    );
  }

  void _showLocationOnMap(HobbyGroupModel group) {
    if (group.latitude == null || group.longitude == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Bu hobi grubunun konum bilgisi bulunmuyor.'),
          duration: Duration(seconds: 2),
        ),
      );
      return;
    }

    final marker = MapMarker(
      position: LatLng(group.latitude!, group.longitude!),
      title: group.name,
      subtitle: group.category,
      icon: Container(
        padding: const EdgeInsets.all(6),
        decoration: BoxDecoration(
          color: Colors.pink,
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
        child: const Icon(Icons.group, color: Colors.white, size: 24),
      ),
    );

    showDialog(
      context: context,
      builder: (context) => LocationFilterDialog(
        initialLatitude: group.latitude!,
        initialLongitude: group.longitude!,
        initialRadiusKm: 10.0,
        onApply: (latitude, longitude, radiusKm) {
          // Sadece gösterim için, filtreleme yapmıyoruz
          Navigator.of(context).pop();
        },
        markers: [marker],
      ),
    );
  }
}

