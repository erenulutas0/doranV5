import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

/// Keşfet / Giriş ekranı
/// Kullanıcıyı dört dikey arasında seçim yapmaya yönlendirir.
class ExplorePage extends StatelessWidget {
  const ExplorePage({super.key});

  @override
  Widget build(BuildContext context) {
    const scaffoldBg = Color(0xFF0B0B0B);
    const cardBg = Color(0xFF121212);
    const neonGreen = Color(0xFFA4F22E);
    final items = [
      _ExploreItem(
        title: 'Products',
        subtitle: 'Ürünleri keşfet',
        icon: Icons.shopping_cart_outlined,
        route: '/products',
      ),
      _ExploreItem(
        title: 'Shops',
        subtitle: 'Mağazaları keşfet',
        icon: Icons.store_mall_directory,
        route: '/shops',
      ),
      _ExploreItem(
        title: 'Jobs',
        subtitle: 'İş ilanlarına göz at',
        icon: Icons.work_outline,
        route: '/jobs',
      ),
      _ExploreItem(
        title: 'Mekanlar',
        subtitle: 'Eğlence ve etkinlikler',
        icon: Icons.location_city,
        route: '/entertainment',
      ),
      _ExploreItem(
        title: 'Hobiler',
        subtitle: 'Topluluklara katıl',
        icon: Icons.group_outlined,
        route: '/hobby-groups',
      ),
    ];

    return Scaffold(
      backgroundColor: scaffoldBg,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 12),
              Row(
                children: [
                  IconButton(
                    icon: const Icon(Icons.arrow_back, color: Colors.white),
                    onPressed: () {
                      if (context.canPop()) {
                        context.pop();
                      } else {
                        context.go('/home');
                      }
                    },
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'Keşfet',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontWeight: FontWeight.w700,
                          color: Colors.white,
                        ),
                  ),
                  const Spacer(),
                ],
              ),
              const SizedBox(height: 6),
              Text(
                'İhtiyacına göre bir alan seç ve devam et',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: Colors.white70,
                    ),
              ),
              const SizedBox(height: 16),
              Expanded(
                child: GridView.builder(
                  physics: const BouncingScrollPhysics(),
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 12,
                    mainAxisSpacing: 12,
                    childAspectRatio: 1.0,
                  ),
                  itemCount: items.length,
                  itemBuilder: (context, index) {
                    final item = items[index];
                    return _ExploreCard(
                      item: item,
                      neonGreen: neonGreen,
                      cardBg: cardBg,
                    );
                  },
                ),
              ),
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () => context.go('/home'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: neonGreen,
                    foregroundColor: Colors.black,
                    padding: const EdgeInsets.symmetric(vertical: 14),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: const Text(
                    'Ana Sayfaya Git',
                    style: TextStyle(fontWeight: FontWeight.w700),
                  ),
                ),
              ),
              const SizedBox(height: 12),
            ],
          ),
        ),
      ),
    );
  }
}

class _ExploreItem {
  final String title;
  final String subtitle;
  final IconData icon;
  final String route;

  _ExploreItem({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.route,
  });
}

class _ExploreCard extends StatelessWidget {
  final _ExploreItem item;
  final Color neonGreen;
  final Color cardBg;

  const _ExploreCard({
    required this.item,
    required this.neonGreen,
    required this.cardBg,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(16),
      onTap: () => context.go('${item.route}?from=explore'),
      child: Container(
        decoration: BoxDecoration(
          color: cardBg,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: Colors.white12),
        ),
        padding: const EdgeInsets.all(14),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: neonGreen,
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(
                    color: neonGreen.withOpacity(0.25),
                    blurRadius: 10,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: Icon(item.icon, color: Colors.black, size: 22),
            ),
            const SizedBox(height: 12),
            Text(
              item.title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w700,
                    color: Colors.white,
                  ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 6),
            Text(
              item.subtitle,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Colors.white70,
                  ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}

