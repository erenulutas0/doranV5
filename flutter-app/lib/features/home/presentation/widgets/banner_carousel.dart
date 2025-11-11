import 'package:flutter/material.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

class BannerCarousel extends StatefulWidget {
  const BannerCarousel({super.key});

  @override
  State<BannerCarousel> createState() => _BannerCarouselState();
}

class _BannerCarouselState extends State<BannerCarousel> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  final List<Map<String, dynamic>> banners = [
    {
      'title': 'Summer Sale',
      'subtitle': 'Up to 50% OFF',
      'color': const Color(0xFF6750A4),
    },
    {
      'title': 'New Arrivals',
      'subtitle': 'Latest Collection',
      'color': const Color(0xFF9575CD),
    },
    {
      'title': 'Free Shipping',
      'subtitle': 'On orders over \$100',
      'color': const Color(0xFF7D5260),
    },
  ];

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isMobile = screenWidth < 600;
    final bannerHeight = isMobile ? 140.0 : 180.0;
    
    return Column(
      children: [
        SizedBox(
          height: bannerHeight,
          child: PageView.builder(
            controller: _pageController,
            onPageChanged: (index) {
              setState(() {
                _currentPage = index;
              });
            },
            itemCount: banners.length,
            itemBuilder: (context, index) {
              final banner = banners[index];
              return Padding(
                padding: EdgeInsets.symmetric(horizontal: isMobile ? 12 : 16),
                child: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [
                        banner['color'] as Color,
                        (banner['color'] as Color).withOpacity(0.7),
                      ],
                    ),
                    borderRadius: BorderRadius.circular(20),
                    boxShadow: [
                      BoxShadow(
                        color: (banner['color'] as Color).withOpacity(0.3),
                        blurRadius: 10,
                        offset: const Offset(0, 4),
                      ),
                    ],
                  ),
                  child: Padding(
                    padding: EdgeInsets.all(isMobile ? 16 : 24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        // Birleştirilmiş metin - daha kompakt ve vurgulu
                        RichText(
                          text: TextSpan(
                            children: [
                              TextSpan(
                                text: banner['title'] as String,
                                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                  fontSize: isMobile ? 22 : 28,
                                ),
                              ),
                              TextSpan(
                                text: ' ${banner['subtitle'] as String}',
                                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                  fontSize: isMobile ? 20 : 26,
                                  letterSpacing: 0.5,
                                ),
                              ),
                            ],
                          ),
                        ),
                        SizedBox(height: isMobile ? 12 : 16),
                        // Yüksek kontrastlı "Shop Now" butonu
                        ElevatedButton(
                          onPressed: () {
                            // Navigate to products
                          },
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFFFD700), // Açık sarı - yüksek kontrast
                            foregroundColor: const Color(0xFF1A1A1A), // Koyu metin
                            elevation: 2,
                            padding: EdgeInsets.symmetric(
                              horizontal: isMobile ? 20 : 28,
                              vertical: isMobile ? 12 : 16,
                            ),
                            textStyle: TextStyle(
                              fontSize: isMobile ? 13 : 16,
                              fontWeight: FontWeight.bold,
                            ),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                          ),
                          child: const Text('Shop Now'),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
          ),
        ),
        const SizedBox(height: 12),
        SmoothPageIndicator(
          controller: _pageController,
          count: banners.length,
          effect: WormEffect(
            dotColor: Theme.of(context).dividerColor.withOpacity(0.5),
            activeDotColor: Theme.of(context).colorScheme.primary,
            dotHeight: 10, // Daha büyük
            dotWidth: 10, // Daha büyük
            spacing: 10, // Daha fazla boşluk
            radius: 5,
          ),
        ),
      ],
    );
  }
}

