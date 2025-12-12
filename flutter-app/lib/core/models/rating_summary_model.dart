class RatingSummary {
  final String productId;
  final double averageRating;
  final int totalReviews;
  final int star1Count;
  final int star2Count;
  final int star3Count;
  final int star4Count;
  final int star5Count;

  RatingSummary({
    required this.productId,
    required this.averageRating,
    required this.totalReviews,
    this.star1Count = 0,
    this.star2Count = 0,
    this.star3Count = 0,
    this.star4Count = 0,
    this.star5Count = 0,
  });

  factory RatingSummary.fromJson(Map<String, dynamic> json) {
    return RatingSummary(
      productId: json['productId']?.toString() ?? '',
      averageRating: json['averageRating'] is double 
          ? json['averageRating'] as double
          : json['averageRating'] != null 
              ? double.tryParse(json['averageRating'].toString()) ?? 0.0
              : 0.0,
      totalReviews: json['totalReviews'] is int 
          ? json['totalReviews'] as int
          : json['totalReviews'] != null 
              ? int.tryParse(json['totalReviews'].toString()) ?? 0
              : 0,
      star1Count: json['star1Count'] is int ? json['star1Count'] as int : 0,
      star2Count: json['star2Count'] is int ? json['star2Count'] as int : 0,
      star3Count: json['star3Count'] is int ? json['star3Count'] as int : 0,
      star4Count: json['star4Count'] is int ? json['star4Count'] as int : 0,
      star5Count: json['star5Count'] is int ? json['star5Count'] as int : 0,
    );
  }

  double getStarPercentage(int star) {
    if (totalReviews == 0) return 0.0;
    int count = 0;
    switch (star) {
      case 1:
        count = star1Count;
        break;
      case 2:
        count = star2Count;
        break;
      case 3:
        count = star3Count;
        break;
      case 4:
        count = star4Count;
        break;
      case 5:
        count = star5Count;
        break;
    }
    return (count * 100.0) / totalReviews;
  }
  
  int getStarCount(int star) {
    switch (star) {
      case 1:
        return star1Count;
      case 2:
        return star2Count;
      case 3:
        return star3Count;
      case 4:
        return star4Count;
      case 5:
        return star5Count;
      default:
        return 0;
    }
  }
}

