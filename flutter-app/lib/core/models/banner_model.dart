class BannerModel {
  final String id;
  final String imageUrl;
  final String title;
  final String? subtitle;
  final String? actionUrl;

  BannerModel({
    required this.id,
    required this.imageUrl,
    required this.title,
    this.subtitle,
    this.actionUrl,
  });

  factory BannerModel.fromJson(Map<String, dynamic> json) {
    return BannerModel(
      id: json['id'] as String,
      imageUrl: json['imageUrl'] as String,
      title: json['title'] as String,
      subtitle: json['subtitle'] as String?,
      actionUrl: json['actionUrl'] as String?,
    );
  }
}

