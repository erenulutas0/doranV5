import '../models/banner_model.dart';
import '../models/shop_model.dart';

class MockData {
  // Campaign Banners
  static final List<BannerModel> banners = [
    BannerModel(
      id: '1',
      imageUrl: 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800',
      title: 'Summer Sale Up to 50% OFF',
      subtitle: 'Get amazing deals on electronics',
    ),
    BannerModel(
      id: '2',
      imageUrl: 'https://images.unsplash.com/photo-1607083206968-13611e3d76db?w=800',
      title: 'New Arrivals',
      subtitle: 'Check out the latest products',
    ),
    BannerModel(
      id: '3',
      imageUrl: 'https://images.unsplash.com/photo-1607082349566-187342175e2f?w=800',
      title: 'Free Shipping',
      subtitle: 'On orders over \$50',
    ),
  ];

  // Nearby Shops
  static final List<ShopModel> nearbyShops = [
    ShopModel(
      id: '1',
      ownerId: 'owner-1',
      name: 'Tech Electronics',
      category: 'Electronics',
      coverImageUrl: 'https://images.unsplash.com/photo-1601524909162-ae8725290836?w=400',
      averageRating: 4.5,
      reviewCount: 120,
      city: 'Kozhikode',
      isActive: true,
    ),
    ShopModel(
      id: '2',
      ownerId: 'owner-2',
      name: 'Fashion Hub',
      category: 'Fashion & Apparel',
      coverImageUrl: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400',
      averageRating: 4.8,
      reviewCount: 95,
      city: 'Kozhikode',
      isActive: true,
    ),
    ShopModel(
      id: '3',
      ownerId: 'owner-3',
      name: 'Fresh Grocery',
      category: 'Grocery & Food',
      coverImageUrl: 'https://images.unsplash.com/photo-1579113800032-c38bd7635818?w=400',
      averageRating: 4.3,
      reviewCount: 78,
      city: 'Kozhikode',
      isActive: true,
    ),
    ShopModel(
      id: '4',
      ownerId: 'owner-4',
      name: 'Book Corner',
      category: 'Books & Stationery',
      coverImageUrl: 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?w=400',
      averageRating: 4.6,
      reviewCount: 56,
      city: 'Kozhikode',
      isActive: true,
    ),
    ShopModel(
      id: '5',
      ownerId: 'owner-5',
      name: 'Sports Arena',
      category: 'Sports & Fitness',
      coverImageUrl: 'https://images.unsplash.com/photo-1556906781-9a412961c28c?w=400',
      averageRating: 4.4,
      reviewCount: 89,
      city: 'Kozhikode',
      isActive: true,
    ),
    ShopModel(
      id: '6',
      ownerId: 'owner-6',
      name: 'Beauty Palace',
      category: 'Beauty & Cosmetics',
      coverImageUrl: 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=400',
      averageRating: 4.7,
      reviewCount: 134,
      city: 'Kozhikode',
      isActive: true,
    ),
  ];
}

