class Review {
  final String id;
  final String productId;
  final String userId;
  final String userName;
  final int rating;
  final String? comment;
  final bool isApproved;
  final int helpfulCount;
  final DateTime createdAt;
  final DateTime updatedAt;
  final bool likedByUser; // Kullanıcı bu yorumu beğenmiş mi?

  Review({
    required this.id,
    required this.productId,
    required this.userId,
    required this.userName,
    required this.rating,
    this.comment,
    this.isApproved = true,
    this.helpfulCount = 0,
    required this.createdAt,
    required this.updatedAt,
    this.likedByUser = false,
  });

  factory Review.fromJson(Map<String, dynamic> json) {
    return Review(
      id: json['id']?.toString() ?? '',
      productId: json['productId']?.toString() ?? '',
      userId: json['userId']?.toString() ?? '',
      userName: json['userName'] ?? '',
      rating: json['rating'] is int 
          ? json['rating'] as int
          : json['rating'] != null 
              ? int.tryParse(json['rating'].toString()) ?? 0
              : 0,
      comment: json['comment'],
      isApproved: json['isApproved'] ?? true,
      helpfulCount: json['helpfulCount'] is int 
          ? json['helpfulCount'] as int
          : json['helpfulCount'] != null 
              ? int.tryParse(json['helpfulCount'].toString()) ?? 0
              : 0,
      createdAt: _parseDateTime(json['createdAt']),
      updatedAt: _parseDateTime(json['updatedAt']),
      likedByUser: json['likedByUser'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'productId': productId,
      'userId': userId,
      'userName': userName,
      'rating': rating,
      'comment': comment,
      'isApproved': isApproved,
      'helpfulCount': helpfulCount,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'likedByUser': likedByUser,
    };
  }

  static DateTime _parseDateTime(dynamic dateTime) {
    if (dateTime == null) {
      return DateTime.now();
    }
    try {
      if (dateTime is String) {
        // ISO 8601 formatını parse et
        return DateTime.parse(dateTime);
      } else if (dateTime is int) {
        // Unix timestamp
        return DateTime.fromMillisecondsSinceEpoch(dateTime);
      } else {
        return DateTime.now();
      }
    } catch (e) {
      // Parse hatası durumunda şu anki zamanı döndür
      return DateTime.now();
    }
  }
}

