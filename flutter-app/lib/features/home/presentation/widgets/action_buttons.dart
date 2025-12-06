import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../../../../core/providers/auth_provider.dart';

class ActionButtons extends StatelessWidget {
  const ActionButtons({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = context.watch<AuthProvider>();
    final isAuthenticated = authProvider.isAuthenticated;

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Column(
        children: [
          // First Row: Shop, Jobs, Own Product (if authenticated)
          Row(
            children: [
              Expanded(
                child: _ActionButton(
                  label: 'Shop',
                  icon: Icons.shopping_bag_outlined,
                  color: Theme.of(context).colorScheme.primary,
                  onTap: () {
                    debugPrint('Navigating to /shops');
                    context.push('/shops');
                  },
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _ActionButton(
                  label: 'Jobs',
                  icon: Icons.work_outline,
                  color: Theme.of(context).colorScheme.secondary,
                  onTap: () {
                    debugPrint('Navigating to /jobs');
                    context.push('/jobs');
                  },
                ),
              ),
              // Own Product sadece giriş yapıldığında göster
              if (isAuthenticated) ...[
                const SizedBox(width: 12),
                Expanded(
                  child: _ActionButton(
                    label: 'Own Product',
                    icon: Icons.add_business_outlined,
                    color: Colors.orange,
                    onTap: () {
                      debugPrint('Navigating to /own-products');
                      context.push('/own-products');
                    },
                  ),
                ),
              ],
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Second Row: Mekanlar, Hobi Buluşmaları
          Row(
            children: [
              Expanded(
                child: _ActionButton(
                  label: 'Mekanlar',
                  icon: Icons.location_city_outlined,
                  color: const Color(0xFF9C27B0), // Purple
                  onTap: () {
                    debugPrint('Navigating to /entertainment');
                    context.push('/entertainment');
                  },
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _ActionButton(
                  label: 'Hobi',
                  icon: Icons.group_outlined,
                  color: const Color(0xFF00897B), // Teal
                  onTap: () {
                    debugPrint('Navigating to /hobby-groups');
                    context.push('/hobby-groups');
                  },
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _ActionButton extends StatelessWidget {
  final String label;
  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  const _ActionButton({
    required this.label,
    required this.icon,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      color: color,
      borderRadius: BorderRadius.circular(12),
      elevation: 2,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 12),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                icon,
                color: Colors.white,
                size: 20,
              ),
              const SizedBox(width: 6),
              Text(
                label,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

