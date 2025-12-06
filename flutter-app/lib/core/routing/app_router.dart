import 'package:go_router/go_router.dart';

import '../../features/home/presentation/pages/home_page.dart';
import '../../features/product/presentation/pages/product_detail_page.dart';
import '../../features/cart/presentation/pages/cart_page.dart';
import '../../features/order/presentation/pages/orders_page.dart';
import '../../features/profile/presentation/pages/profile_page.dart';
import '../../features/profile/presentation/pages/edit_profile_page.dart';
import '../../features/profile/presentation/pages/addresses_page.dart';
import '../../features/profile/presentation/pages/settings_page.dart';
import '../../features/profile/presentation/pages/help_support_page.dart';
import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/auth/presentation/pages/register_page.dart';
import '../../features/splash/presentation/pages/splash_page.dart';
import '../../features/onboarding/presentation/pages/onboarding_page.dart';
import '../../features/intro/presentation/pages/intro_page.dart';
import '../../features/shops/presentation/pages/shops_page.dart';
import '../../features/jobs/presentation/pages/jobs_page.dart';
import '../../features/own-products/presentation/pages/own_products_page.dart';
import '../../features/entertainment/presentation/pages/entertainment_page.dart';
import '../../features/hobby-groups/presentation/pages/hobby_groups_page.dart';

class AppRouter {
  static final GoRouter router = GoRouter(
    initialLocation: '/splash',
    routes: [
      GoRoute(
        path: '/splash',
        builder: (context, state) => const SplashPage(),
      ),
      GoRoute(
        path: '/intro',
        builder: (context, state) => const IntroPage(),
      ),
      GoRoute(
        path: '/onboarding',
        builder: (context, state) => const OnboardingPage(),
      ),
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterPage(),
      ),
      GoRoute(
        path: '/home',
        builder: (context, state) => const HomePage(),
      ),
      GoRoute(
        path: '/product/:id',
        builder: (context, state) {
          final productId = state.pathParameters['id']!;
          return ProductDetailPage(productId: productId);
        },
      ),
      GoRoute(
        path: '/cart',
        builder: (context, state) => const CartPage(),
      ),
      GoRoute(
        path: '/orders',
        builder: (context, state) => const OrdersPage(),
      ),
      GoRoute(
        path: '/profile',
        builder: (context, state) => const ProfilePage(),
      ),
      GoRoute(
        path: '/profile/edit',
        builder: (context, state) => const EditProfilePage(),
      ),
      GoRoute(
        path: '/profile/addresses',
        builder: (context, state) => const AddressesPage(),
      ),
      GoRoute(
        path: '/profile/settings',
        builder: (context, state) => const SettingsPage(),
      ),
      GoRoute(
        path: '/profile/help',
        builder: (context, state) => const HelpSupportPage(),
      ),
      GoRoute(
        path: '/shops',
        builder: (context, state) => const ShopsPage(),
      ),
      GoRoute(
        path: '/jobs',
        builder: (context, state) => const JobsPage(),
      ),
      GoRoute(
        path: '/own-products',
        builder: (context, state) => const OwnProductsPage(),
      ),
      GoRoute(
        path: '/entertainment',
        builder: (context, state) => const EntertainmentPage(),
      ),
      GoRoute(
        path: '/hobby-groups',
        builder: (context, state) => const HobbyGroupsPage(),
      ),
    ],
  );
}

