class ShopModel {
  final String id;
  final String ownerId;
  final String name;
  final String? description;
  final String category;
  final String? address;
  final String? city;
  final String? district;
  final String? postalCode;
  final String? phone;
  final String? email;
  final String? website;
  final double? latitude;
  final double? longitude;
  final String? openingTime;
  final String? closingTime;
  final String? workingDays;
  final String? logoImageUrl;
  final String? coverImageUrl;
  final double? averageRating;
  final int? reviewCount;
  final bool? isActive;
  final String? createdAt;
  final String? updatedAt;

  ShopModel({
    required this.id,
    required this.ownerId,
    required this.name,
    this.description,
    required this.category,
    this.address,
    this.city,
    this.district,
    this.postalCode,
    this.phone,
    this.email,
    this.website,
    this.latitude,
    this.longitude,
    this.openingTime,
    this.closingTime,
    this.workingDays,
    this.logoImageUrl,
    this.coverImageUrl,
    this.averageRating,
    this.reviewCount,
    this.isActive,
    this.createdAt,
    this.updatedAt,
  });

  factory ShopModel.fromJson(Map<String, dynamic> json) {
    final category = json['category']?.toString() ?? '';
    final coverImageUrl = json['coverImageUrl'] as String?;
    final logoImageUrl = json['logoImageUrl'] as String?;
    
    // Dummy Unsplash URLs for demonstration if no image URL provided
    String? getDummyCoverImage(String category) {
      if (coverImageUrl != null && coverImageUrl!.isNotEmpty) {
        return coverImageUrl;
      }
      // Category-based dummy images from Unsplash
      final categoryImages = {
        'Electronics': 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=800&h=400&fit=crop',
        'Fashion': 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800&h=400&fit=crop',
        'Food': 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&h=400&fit=crop',
        'Home & Garden': 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&h=400&fit=crop',
        'Sports': 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop',
        'Books': 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=800&h=400&fit=crop',
        'Beauty': 'https://images.unsplash.com/photo-1522338242992-e1a54906a8da?w=800&h=400&fit=crop',
        'Pets': 'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=800&h=400&fit=crop',
        'Musical Instruments': 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=400&fit=crop',
      };
      return categoryImages[category] ?? 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800&h=400&fit=crop';
    }
    
    return ShopModel(
      id: json['id']?.toString() ?? '',
      ownerId: json['ownerId']?.toString() ?? '',
      name: json['name'] ?? '',
      description: json['description'],
      category: category,
      address: json['address'],
      city: json['city'],
      district: json['district'],
      postalCode: json['postalCode'],
      phone: json['phone'],
      email: json['email'],
      website: json['website'],
      latitude: json['latitude']?.toDouble(),
      longitude: json['longitude']?.toDouble(),
      openingTime: json['openingTime'],
      closingTime: json['closingTime'],
      workingDays: json['workingDays'],
      logoImageUrl: logoImageUrl,
      coverImageUrl: getDummyCoverImage(category),
      averageRating: json['averageRating']?.toDouble(),
      reviewCount: json['reviewCount'],
      isActive: json['isActive'],
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
    );
  }

  String get displayImage {
    // Prefer cover image, fallback to logo, then placeholder
    if (coverImageUrl != null && coverImageUrl!.isNotEmpty) {
      return coverImageUrl!;
    }
    if (logoImageUrl != null && logoImageUrl!.isNotEmpty) {
      return logoImageUrl!;
    }
    // Return empty string for placeholder
    return '';
  }
  double get rating => averageRating ?? 0.0;
  int get reviews => reviewCount ?? 0;
  bool get isVerified => isActive ?? false;
}
