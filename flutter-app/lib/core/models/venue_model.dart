class VenueModel {
  final String id;
  final String name;
  final String? description;
  final String venueType; // RESTAURANT, CAFE, BAR, CLUB, THEATER, CINEMA, SPORTS, OTHER
  final String? address;
  final String? city;
  final String? district;
  final String? postalCode;
  final String? phone;
  final String? email;
  final String? website;
  final double? latitude;
  final double? longitude;
  final String? openingHours;
  final String? imageUrl;
  final double? averageRating;
  final int? reviewCount;
  final bool? isActive;
  final String? createdAt;
  final String? updatedAt;

  VenueModel({
    required this.id,
    required this.name,
    this.description,
    required this.venueType,
    this.address,
    this.city,
    this.district,
    this.postalCode,
    this.phone,
    this.email,
    this.website,
    this.latitude,
    this.longitude,
    this.openingHours,
    this.imageUrl,
    this.averageRating,
    this.reviewCount,
    this.isActive,
    this.createdAt,
    this.updatedAt,
  });

  factory VenueModel.fromJson(Map<String, dynamic> json) {
    final venueType = json['venueType']?.toString() ?? 'OTHER';
    final imageUrl = json['imageUrl'] as String?;
    final coverImageUrl = json['coverImageUrl'] as String?;
    
    // Dummy Unsplash URLs for demonstration if no image URL provided
    String? getDummyImageUrl(String type) {
      if (coverImageUrl != null && coverImageUrl!.isNotEmpty) {
        return coverImageUrl;
      }
      if (imageUrl != null && imageUrl!.isNotEmpty) {
        return imageUrl;
      }
      // Venue type-based dummy images from Unsplash
      final typeImages = {
        'RESTAURANT': 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&h=400&fit=crop',
        'CAFE': 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&h=400&fit=crop',
        'BAR': 'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800&h=400&fit=crop',
        'CLUB': 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800&h=400&fit=crop',
        'THEATER': 'https://images.unsplash.com/photo-1503095396549-807759245b35?w=800&h=400&fit=crop',
        'CINEMA': 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&h=400&fit=crop',
        'SPORTS': 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop',
        'OTHER': 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=400&fit=crop',
      };
      return typeImages[type.toUpperCase()] ?? 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=400&fit=crop';
    }
    
    return VenueModel(
      id: json['id']?.toString() ?? '',
      name: json['name'] ?? '',
      description: json['description'],
      venueType: venueType,
      address: json['address'],
      city: json['city'],
      district: json['district'],
      postalCode: json['postalCode'],
      phone: json['phone'],
      email: json['email'],
      website: json['website'],
      latitude: json['latitude']?.toDouble(),
      longitude: json['longitude']?.toDouble(),
      openingHours: json['openingHours'],
      imageUrl: getDummyImageUrl(venueType),
      averageRating: json['averageRating']?.toDouble(),
      reviewCount: json['reviewCount'],
      isActive: json['isActive'],
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
    );
  }

  String get displayType {
    switch (venueType.toUpperCase()) {
      case 'RESTAURANT':
        return 'Restaurant';
      case 'CAFE':
        return 'Cafe';
      case 'BAR':
        return 'Bar';
      case 'CLUB':
        return 'Club';
      case 'THEATER':
        return 'Theater';
      case 'CINEMA':
        return 'Cinema';
      case 'SPORTS':
        return 'Sports';
      default:
        return 'Other';
    }
  }

  double get rating => averageRating ?? 0.0;
  int get reviews => reviewCount ?? 0;
}

