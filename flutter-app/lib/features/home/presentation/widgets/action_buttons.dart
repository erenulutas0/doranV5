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
          // First Row: Products, Shop, Jobs
          Row(
            children: [
              Expanded(
                child: _ActionButton(
                  label: 'Products',
                  icon: Icons.shopping_cart_outlined,
                  backgroundColor: const Color(0xFF1E1E1E),
                  accentColor: const Color(0xFFCCFF00),
                  onTap: () {
                    debugPrint('Navigating to /products');
                    context.go('/products?from=home');
                  },
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _ActionButton(
                  label: 'Shop',
                  icon: Icons.shopping_bag_outlined,
                  backgroundColor: const Color(0xFF1E1E1E),
                  accentColor: const Color(0xFFCCFF00),
                  onTap: () {
                    debugPrint('Navigating to /shops');
                    context.go('/shops?from=home');
                  },
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _ActionButton(
                  label: 'Jobs',
                  icon: Icons.work_outline,
                  backgroundColor: const Color(0xFF1E1E1E),
                  accentColor: const Color(0xFFCCFF00),
                  onTap: () {
                    debugPrint('Navigating to /jobs');
                    context.go('/jobs?from=home');
                  },
                ),
              ),
            ],
          ),
          
          const SizedBox(height: 12),
          
          // Second Row: Mekanlar, Hobi, Own Product (if authenticated)
          Row(
            children: [
              Expanded(
                child: _ActionButton(
                  label: 'Mekanlar',
                  icon: Icons.location_city_outlined,
                  backgroundColor: const Color(0xFF1E1E1E),
                  accentColor: const Color(0xFFCCFF00),
                  onTap: () {
                    debugPrint('Navigating to /entertainment');
                    context.go('/entertainment?from=home');
                  },
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _ActionButton(
                  label: 'Hobi',
                  icon: Icons.group_outlined,
                  backgroundColor: const Color(0xFF1E1E1E),
                  accentColor: const Color(0xFFCCFF00),
                  onTap: () {
                    debugPrint('Navigating to /hobby-groups');
                    context.go('/hobby-groups?from=home');
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
                    backgroundColor: const Color(0xFF1E1E1E),
                    accentColor: const Color(0xFFCCFF00),
                    onTap: () {
                      debugPrint('Navigating to /own-products');
                      context.push('/own-products');
                    },
                  ),
                ),
              ],
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
  final Color backgroundColor;
  final Color accentColor;
  final VoidCallback onTap;

  const _ActionButton({
    required this.label,
    required this.icon,
    required this.backgroundColor,
    required this.accentColor,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      color: backgroundColor,
      borderRadius: BorderRadius.circular(12),
      elevation: 2,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 12),
          decoration: BoxDecoration(
            color: backgroundColor,
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: accentColor, width: 1.2),
            boxShadow: [
              BoxShadow(
                color: accentColor.withOpacity(0.18),
                blurRadius: 10,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                icon,
                color: accentColor,
                size: 20,
              ),
              const SizedBox(width: 6),
              Text(
                label,
                style: TextStyle(
                  color: accentColor,
                  fontSize: 14,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

