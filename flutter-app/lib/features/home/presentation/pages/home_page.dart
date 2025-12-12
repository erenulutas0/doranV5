import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:go_router/go_router.dart';

import 'package:geolocator/geolocator.dart';
import '../../../../core/providers/product_provider.dart';
import '../../../../core/providers/cart_provider.dart';
import '../../../../core/providers/auth_provider.dart';
import '../../../../core/data/mock_data.dart';
import '../../../../core/services/api_service.dart';
import '../../../../core/services/location_service.dart';
import '../../../../core/models/shop_model.dart';
import '../../../../core/models/job_model.dart';
import '../../../../core/models/hobby_group_model.dart';
import '../../../../core/models/venue_model.dart';
import '../widgets/location_header.dart';
import '../widgets/search_bar_widget.dart';
import '../widgets/action_buttons.dart';
import '../widgets/banner_slider.dart';
import '../widgets/nearby_shops_section.dart';
import '../widgets/product_grid.dart';
import '../widgets/product_grid_skeleton.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

enum _NearbyKind { shops, jobs, hobby, venues }

class _HomePageState extends State<HomePage> {
  final ScrollController _scrollController = ScrollController();
  final ApiService _apiService = ApiService();

  String _locationTitle = 'Konum kapalı';
  String _locationSubtitle = 'Yakını göster için izin verin';
  bool _locationAllowed = false;
  String? _detectedCity;

  List<ShopModel> _nearbyShops = [];
  bool _shopsLoading = false;
  String? _shopsError;

  List<JobModel> _nearbyJobs = [];
  bool _jobsLoading = false;
  String? _jobsError;

  List<HobbyGroupModel> _nearbyHobbies = [];
  bool _hobbiesLoading = false;
  String? _hobbiesError;

  List<VenueModel> _nearbyVenues = [];
  bool _venuesLoading = false;
  String? _venuesError;

  _NearbyKind _selectedNearby = _NearbyKind.shops;

  @override
  void initState() {
    super.initState();
    // Build tamamlandıktan sonra ürünleri yükle
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadProducts();
      _loadCachedLocation();
      _initLocationAndShops(requestPermission: false);
      // Jobs and hobby are location-independent; load lazily on demand
    });
  }

  Future<void> _loadCachedLocation() async {
    final cached = await LocationService.getCached();
    if (cached == null) return;
    if (!mounted) return;
    setState(() {
      _detectedCity = cached.city ?? _detectedCity;
      if (cached.city != null && cached.city!.isNotEmpty) {
        _locationTitle = 'Konum açık';
        _locationSubtitle = cached.city!;
        _locationAllowed = true;
      }
    });
  }

  void _loadProducts() {
    final productProvider = context.read<ProductProvider>();
    productProvider.fetchProducts();
  }

  Future<bool> _ensurePermission({required bool request}) async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      setState(() {
        _locationAllowed = false;
        _locationTitle = 'Konum servisi kapalı';
        _locationSubtitle = 'Açarsanız yakın mağazaları gösteririz';
      });
      return false;
    }

    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied && request) {
      permission = await Geolocator.requestPermission();
    }

    if (permission == LocationPermission.denied ||
        permission == LocationPermission.deniedForever) {
      setState(() {
        _locationAllowed = false;
        _locationTitle = 'Konum izni yok';
        _locationSubtitle = 'İzin vermezseniz yakın mağazalar gizli kalır';
      });
      return false;
    }

    setState(() {
      _locationAllowed = true;
      _locationTitle = 'Konum açık';
      _locationSubtitle = 'Konum alınıyor...';
    });
    return true;
  }

  Future<void> _initLocationAndShops({required bool requestPermission}) async {
    setState(() {
      _shopsLoading = true;
      _shopsError = null;
    });

    try {
      final allowed = await _ensurePermission(request: requestPermission);
      if (!allowed) {
        setState(() {
          _nearbyShops = [];
        });
        return;
      }

      // Fast location: last known first, then quick fallback (2s max)
      final snap = await LocationService.getFast(requestPermission: requestPermission);
      if (snap != null && mounted) {
        setState(() {
          _detectedCity = snap.city ?? _detectedCity;
          if (snap.city != null && snap.city!.isNotEmpty) {
            _locationTitle = 'Konum açık';
            _locationSubtitle = snap.city!;
          }
        });
      }

      // Load shops (currently no lat/lon API; fallback to general list)
      final shops = await _apiService.getShops(page: 0, size: 10);
      setState(() {
        _nearbyShops = shops;
      });

      // Preload venues with city hint if available
      _loadVenuesIfNeeded();
    } catch (e) {
      setState(() {
        _shopsError = e.toString();
      });
    } finally {
      if (mounted) {
        setState(() {
          _shopsLoading = false;
        });
      }
    }
  }

  Future<void> _loadJobsIfNeeded() async {
    if (_nearbyJobs.isNotEmpty || _jobsLoading) return;
    setState(() {
      _jobsLoading = true;
      _jobsError = null;
    });
    try {
      final jobs = await _apiService.getJobs(page: 0, size: 10);
      setState(() {
        _nearbyJobs = jobs;
      });
    } catch (e) {
      setState(() {
        _jobsError = e.toString();
      });
    } finally {
      if (mounted) {
        setState(() {
          _jobsLoading = false;
        });
      }
    }
  }

  Future<void> _loadHobbiesIfNeeded() async {
    if (_nearbyHobbies.isNotEmpty || _hobbiesLoading) return;
    setState(() {
      _hobbiesLoading = true;
      _hobbiesError = null;
    });
    try {
      final hobbies = await _apiService.getHobbyGroups(
        page: 0,
        size: 10,
        location: _detectedCity,
      );
      final filtered = (_detectedCity != null)
          ? hobbies.where((h) {
              final loc = h.location?.toLowerCase() ?? '';
              return loc.contains(_detectedCity!.toLowerCase());
            }).toList()
          : hobbies;
      setState(() {
        _nearbyHobbies = filtered.isNotEmpty ? filtered : hobbies;
      });
    } catch (e) {
      setState(() {
        _hobbiesError = e.toString();
      });
    } finally {
      if (mounted) {
        setState(() {
          _hobbiesLoading = false;
        });
      }
    }
  }

  void _onSelectNearby(_NearbyKind kind) {
    setState(() {
      _selectedNearby = kind;
    });
    if (kind == _NearbyKind.jobs) {
      _loadJobsIfNeeded();
    } else if (kind == _NearbyKind.hobby) {
      _loadHobbiesIfNeeded();
    } else if (kind == _NearbyKind.venues) {
      _loadVenuesIfNeeded();
    }
  }

  Future<void> _loadVenuesIfNeeded() async {
    if (_nearbyVenues.isNotEmpty || _venuesLoading) return;
    setState(() {
      _venuesLoading = true;
      _venuesError = null;
    });
    try {
      final venues = await _apiService.getVenues(
        page: 0,
        size: 10,
        city: _detectedCity,
      );
      setState(() {
        _nearbyVenues = venues;
      });
    } catch (e) {
      setState(() {
        _venuesError = e.toString();
      });
    } finally {
      if (mounted) {
        setState(() {
          _venuesLoading = false;
        });
      }
    }
  }

  // City detection moved to LocationService

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      drawer: _buildDrawer(context),
      body: CustomScrollView(
        controller: _scrollController,
        slivers: [
          // Location Header
          SliverToBoxAdapter(
            child: LocationHeader(
              location: _locationTitle,
              address: _locationSubtitle,
              onLocationTap: () {
                _initLocationAndShops(requestPermission: true);
              },
              onRefreshTap: () async {
                // Refresh without showing in-app prompts; OS prompt may appear if denied.
                await _initLocationAndShops(requestPermission: true);
              },
            ),
          ),

          // Search Bar
          SliverToBoxAdapter(
            child: SearchBarWidget(
              onTap: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Search functionality coming soon!'),
                  ),
                );
              },
            ),
          ),

          // Action Buttons (Shop, Jobs, Own Product)
          const SliverToBoxAdapter(
            child: ActionButtons(),
          ),

          const SliverToBoxAdapter(
            child: SizedBox(height: 20),
          ),

          // Banner Slider
          SliverToBoxAdapter(
            child: BannerSlider(
              banners: MockData.banners,
            ),
          ),

          const SliverToBoxAdapter(
            child: SizedBox(height: 20),
          ),

          // Nearby Section with filter (Shops default, Jobs, Hobby) + See All
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
              child: Stack(
                children: [
                  SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    physics: const BouncingScrollPhysics(),
                    child: Padding(
                      padding: const EdgeInsets.only(right: 70),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          _NearbyChip(
                            label: 'Shops',
                            selected: _selectedNearby == _NearbyKind.shops,
                            onTap: () => _onSelectNearby(_NearbyKind.shops),
                          ),
                          const SizedBox(width: 8),
                          _NearbyChip(
                            label: 'Jobs',
                            selected: _selectedNearby == _NearbyKind.jobs,
                            onTap: () => _onSelectNearby(_NearbyKind.jobs),
                          ),
                          const SizedBox(width: 8),
                          _NearbyChip(
                            label: 'Hobby',
                            selected: _selectedNearby == _NearbyKind.hobby,
                            onTap: () => _onSelectNearby(_NearbyKind.hobby),
                          ),
                          const SizedBox(width: 8),
                          _NearbyChip(
                            label: 'Mekan',
                            selected: _selectedNearby == _NearbyKind.venues,
                            onTap: () => _onSelectNearby(_NearbyKind.venues),
                          ),
                        ],
                      ),
                    ),
                  ),
                  Positioned(
                    right: 0,
                    top: 0,
                    bottom: 0,
                    child: Center(
                      child: TextButton(
                        onPressed: () {
                          final params = <String, String>{'from': 'home'};
                          if (_detectedCity != null) {
                            params['city'] = _detectedCity!;
                          }
                          final queryString = params.entries
                              .map((e) => '${Uri.encodeComponent(e.key)}=${Uri.encodeComponent(e.value)}')
                              .join('&');
                          final queryParam = queryString.isNotEmpty ? '?$queryString' : '';
                          switch (_selectedNearby) {
                            case _NearbyKind.shops:
                              context.go('/shops$queryParam');
                              break;
                            case _NearbyKind.jobs:
                              context.go('/jobs$queryParam');
                              break;
                            case _NearbyKind.hobby:
                              context.go('/hobby-groups$queryParam');
                              break;
                            case _NearbyKind.venues:
                              context.go('/entertainment$queryParam');
                              break;
                          }
                        },
                        style: TextButton.styleFrom(
                          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                          minimumSize: const Size(60, 36),
                          tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                        ),
                        child: const Text(
                          'See All',
                          style: TextStyle(
                            color: Color(0xFFCCFF00),
                            fontWeight: FontWeight.w600,
                            fontSize: 14,
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),

          _buildNearbyContent(context),

          const SliverToBoxAdapter(
            child: SizedBox(height: 24),
          ),

          // Featured Products Section Header
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text(
                    'Featured Products',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  TextButton(
                    onPressed: () {
                      context.go('/products?from=home');
                    },
                    style: TextButton.styleFrom(
                      foregroundColor: const Color(0xFFCCFF00),
                    ),
                    child: const Text(
                      'See All',
                      style: TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),

          const SliverToBoxAdapter(
            child: SizedBox(height: 8),
          ),

          // Products Grid
          Consumer<ProductProvider>(
            builder: (context, productProvider, child) {
              if (productProvider.isLoading) {
                return const ProductGridSkeleton();
              }

              if (productProvider.error != null) {
                return SliverFillRemaining(
                  hasScrollBody: false,
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.error_outline,
                          size: 64,
                          color: Theme.of(context).colorScheme.error,
                        ),
                        const SizedBox(height: 16),
                        Text(
                          productProvider.error!,
                          style: Theme.of(context).textTheme.bodyLarge,
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 16),
                        ElevatedButton(
                          onPressed: _loadProducts,
                          child: const Text('Retry'),
                        ),
                      ],
                    ),
                  ),
                );
              }

              final products = productProvider.products;

              if (products.isEmpty) {
                return SliverFillRemaining(
                  hasScrollBody: false,
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.inventory_2_outlined,
                          size: 64,
                          color: Theme.of(context).colorScheme.onSurface.withOpacity(0.3),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'No products found',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              }

              // Show only first 6 products for featured section
              final featuredProducts = products.take(6).toList();

              return ProductGrid(products: featuredProducts);
            },
          ),

          // Bottom Padding
          const SliverToBoxAdapter(
            child: SizedBox(height: 80),
          ),
        ],
      ),
      bottomNavigationBar: _buildBottomNavBar(context),
    );
  }

  Widget _buildDrawer(BuildContext context) {
    final authProvider = context.watch<AuthProvider>();

    return Drawer(
      child: Column(
        children: [
          // Drawer Header
          Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [
                  Theme.of(context).colorScheme.primary,
                  Theme.of(context).colorScheme.primary.withOpacity(0.8),
                ],
              ),
            ),
            padding: const EdgeInsets.fromLTRB(16, 60, 16, 20),
            child: Row(
              children: [
                CircleAvatar(
                  backgroundColor: Colors.white,
                  radius: 35,
                  child: Text(
                    authProvider.userName?.substring(0, 1).toUpperCase() ?? 'U',
                    style: TextStyle(
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Hello,',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.white.withOpacity(0.9),
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        authProvider.userName ?? 'User',
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                      if (authProvider.userEmail != null && authProvider.userEmail!.isNotEmpty) ...[
                        const SizedBox(height: 4),
                        Text(
                          authProvider.userEmail!,
                          style: TextStyle(
                            fontSize: 13,
                            color: Colors.white.withOpacity(0.8),
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ],
                    ],
                  ),
                ),
              ],
            ),
          ),

          // Menu Items
          Expanded(
            child: ListView(
              padding: EdgeInsets.zero,
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Text(
                    'NAVIGATION',
                    style: Theme.of(context).textTheme.labelSmall?.copyWith(
                      color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                      fontWeight: FontWeight.w600,
                      letterSpacing: 1.2,
                    ),
                  ),
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.home_outlined,
                  title: 'Home',
                  onTap: () {
                    Navigator.pop(context);
                    context.go('/home');
                  },
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.explore_outlined,
                  title: 'Keşfet',
                  onTap: () {
                    Navigator.pop(context);
                    context.go('/explore');
                  },
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.shopping_bag_outlined,
                  title: 'Orders',
                  onTap: () {
                    Navigator.pop(context);
                    context.push('/orders');
                  },
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.shopping_cart_outlined,
                  title: 'Cart',
                  badge: context.watch<CartProvider>().itemCount,
                  onTap: () {
                    Navigator.pop(context);
                    context.push('/cart');
                  },
                ),
                const Divider(height: 1),
                
                Padding(
                  padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                  child: Text(
                    'ACCOUNT',
                    style: Theme.of(context).textTheme.labelSmall?.copyWith(
                      color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                      fontWeight: FontWeight.w600,
                      letterSpacing: 1.2,
                    ),
                  ),
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.person_outline,
                  title: 'Profile',
                  onTap: () {
                    Navigator.pop(context);
                    context.push('/profile');
                  },
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.settings_outlined,
                  title: 'Settings',
                  onTap: () {
                    Navigator.pop(context);
                    context.push('/profile/settings');
                  },
                ),
                const Divider(height: 1),
                
                Padding(
                  padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                  child: Text(
                    'SUPPORT',
                    style: Theme.of(context).textTheme.labelSmall?.copyWith(
                      color: Theme.of(context).colorScheme.onSurface.withOpacity(0.6),
                      fontWeight: FontWeight.w600,
                      letterSpacing: 1.2,
                    ),
                  ),
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.help_outline,
                  title: 'Help & Support',
                  onTap: () {
                    Navigator.pop(context);
                    context.push('/profile/help');
                  },
                ),
                _buildDrawerItem(
                  context,
                  icon: Icons.info_outline,
                  title: 'About',
                  onTap: () {
                    Navigator.pop(context);
                    showAboutDialog(
                      context: context,
                      applicationName: 'E-Commerce App',
                      applicationVersion: '1.0.0',
                      applicationIcon: Icon(
                        Icons.shopping_bag,
                        size: 48,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                    );
                  },
                ),
              ],
            ),
          ),

          // Logout Button
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Theme.of(context).scaffoldBackgroundColor,
              border: Border(
                top: BorderSide(
                  color: Theme.of(context).dividerColor,
                  width: 1,
                ),
              ),
            ),
            child: SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: () {
                  Navigator.pop(context);
                  authProvider.logout();
                  context.go('/login');
                },
                icon: const Icon(Icons.logout, color: Colors.white),
                label: const Text(
                  'Logout',
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                  ),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFDC3545),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  elevation: 2,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDrawerItem(
    BuildContext context, {
    required IconData icon,
    required String title,
    required VoidCallback onTap,
    int? badge,
  }) {
    return ListTile(
      leading: Icon(icon, color: Theme.of(context).colorScheme.primary),
      title: Text(title),
      trailing: badge != null && badge > 0
          ? Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.error,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                '$badge',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                ),
              ),
            )
          : null,
      onTap: onTap,
    );
  }

  Widget _buildBottomNavBar(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surface,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(
              Theme.of(context).brightness == Brightness.dark ? 0.3 : 0.05,
            ),
            blurRadius: 10,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: BottomNavigationBar(
        currentIndex: 0,
        onTap: (index) {
          switch (index) {
            case 0:
              // Already on home
              break;
            case 1:
              context.push('/orders');
              break;
            case 2:
              context.push('/profile');
              break;
          }
        },
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.shopping_bag_outlined),
            label: 'Orders',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person_outline),
            label: 'Profile',
          ),
        ],
      ),
    );
  }

  Widget _buildNearbyContent(BuildContext context) {
    switch (_selectedNearby) {
      case _NearbyKind.shops:
        if (!_locationAllowed) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 16, horizontal: 16),
              child: Text(
                'Konum izni vermediniz. Yakın mağazaları görmek için izin verin.',
                style: TextStyle(color: Colors.white70),
              ),
            ),
          );
        }
        if (_shopsLoading) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24),
              child: Center(
                child: CircularProgressIndicator(color: Color(0xFFCCFF00)),
              ),
            ),
          );
        }
        if (_shopsError != null) {
          return SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, color: Colors.redAccent, size: 48),
                  const SizedBox(height: 12),
                  Text(
                    _shopsError!,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.white70),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  ElevatedButton(
                    onPressed: () {
                      _initLocationAndShops(requestPermission: true);
                    },
                    child: const Text('Retry'),
                  ),
                ],
              ),
            ),
          );
        }
        if (_nearbyShops.isEmpty) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Text(
                'No nearby shops found.',
                style: TextStyle(color: Colors.white70),
              ),
            ),
          );
        }
        return SliverToBoxAdapter(
          child: NearbyShopsSection(
            shops: _nearbyShops,
          ),
        );

      case _NearbyKind.jobs:
        if (_jobsLoading) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24),
              child: Center(
                child: CircularProgressIndicator(color: Color(0xFFCCFF00)),
              ),
            ),
          );
        }
        if (_jobsError != null) {
          return SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Column(
                children: [
                  const Icon(Icons.error_outline, color: Colors.redAccent, size: 48),
                  const SizedBox(height: 12),
                  Text(
                    _jobsError!,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.white70),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  ElevatedButton(
                    onPressed: _loadJobsIfNeeded,
                    child: const Text('Retry'),
                  )
                ],
              ),
            ),
          );
        }
        if (_nearbyJobs.isEmpty) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Text(
                'No jobs found.',
                style: TextStyle(color: Colors.white70),
              ),
            ),
          );
        }
        return SliverToBoxAdapter(
          child: _HorizontalCards<JobModel>(
            items: _nearbyJobs,
            itemBuilder: (job) => _SimpleCard(
              title: job.title,
              subtitle: job.companyName ?? job.displayLocation,
              badge: job.displayJobType,
            ),
          ),
        );

      case _NearbyKind.hobby:
        if (_hobbiesLoading) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24),
              child: Center(
                child: CircularProgressIndicator(color: Color(0xFFCCFF00)),
              ),
            ),
          );
        }
        if (_hobbiesError != null) {
          return SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Column(
                children: [
                  const Icon(Icons.error_outline, color: Colors.redAccent, size: 48),
                  const SizedBox(height: 12),
                  Text(
                    _hobbiesError!,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.white70),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  ElevatedButton(
                    onPressed: _loadHobbiesIfNeeded,
                    child: const Text('Retry'),
                  )
                ],
              ),
            ),
          );
        }
        if (_nearbyHobbies.isEmpty) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Text(
                'No hobby groups found.',
                style: TextStyle(color: Colors.white70),
              ),
            ),
          );
        }
        return SliverToBoxAdapter(
          child: _HorizontalCards<HobbyGroupModel>(
            items: _nearbyHobbies,
            itemBuilder: (hg) => _SimpleCard(
              title: hg.name,
              subtitle: hg.location ?? 'Hobi grubu',
              badge: hg.category,
            ),
          ),
        );

      case _NearbyKind.venues:
        if (_venuesLoading) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24),
              child: Center(
                child: CircularProgressIndicator(color: Color(0xFFCCFF00)),
              ),
            ),
          );
        }
        if (_venuesError != null) {
          return SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Column(
                children: [
                  const Icon(Icons.error_outline, color: Colors.redAccent, size: 48),
                  const SizedBox(height: 12),
                  Text(
                    _venuesError!,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.white70),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  ElevatedButton(
                    onPressed: _loadVenuesIfNeeded,
                    child: const Text('Retry'),
                  )
                ],
              ),
            ),
          );
        }
        if (_nearbyVenues.isEmpty) {
          return const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 24, horizontal: 16),
              child: Text(
                'No venues found.',
                style: TextStyle(color: Colors.white70),
              ),
            ),
          );
        }
        return SliverToBoxAdapter(
          child: _HorizontalCards<VenueModel>(
            items: _nearbyVenues,
            itemBuilder: (v) => _SimpleCard(
              title: v.name ?? 'Mekan',
              subtitle: v.city ?? v.address ?? 'Mekan',
              badge: v.venueType ?? 'Venue',
            ),
          ),
        );
    }
    return const SliverToBoxAdapter(child: SizedBox.shrink());
  }
}

class _NearbyChip extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;
  const _NearbyChip({
    required this.label,
    required this.selected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: selected ? const Color(0xFFCCFF00) : const Color(0xFF1E1E1E),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: const Color(0xFF333333)),
        ),
        child: Text(
          label,
          style: TextStyle(
            color: selected ? Colors.black : Colors.white70,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );
  }
}

class _HorizontalCards<T> extends StatelessWidget {
  final List<T> items;
  final Widget Function(T item) itemBuilder;
  const _HorizontalCards({
    required this.items,
    required this.itemBuilder,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 220,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        itemCount: items.length,
        itemBuilder: (context, index) {
          final item = items[index];
          return Padding(
            padding: EdgeInsets.only(right: index == items.length - 1 ? 0 : 12),
            child: itemBuilder(item),
          );
        },
      ),
    );
  }
}

class _SimpleCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final String? badge;

  const _SimpleCard({
    required this.title,
    required this.subtitle,
    required this.badge,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 200,
      decoration: BoxDecoration(
        color: const Color(0xFF1E1E1E),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0xFF333333), width: 0.8),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 8,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              title,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: Colors.white,
              ),
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
            const SizedBox(height: 6),
            Text(
              subtitle,
              style: const TextStyle(
                fontSize: 13,
                color: Colors.white70,
              ),
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
            const Spacer(),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: const Color(0xFFCCFF00).withOpacity(0.12),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFFCCFF00)),
              ),
              child: Text(
                badge ?? '',
                style: const TextStyle(
                  color: Color(0xFFCCFF00),
                  fontWeight: FontWeight.w600,
                  fontSize: 12,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
