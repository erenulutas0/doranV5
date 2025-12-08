import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/product_model.dart';
import '../models/review_model.dart';
import '../models/rating_summary_model.dart';
import '../models/shop_model.dart';
import '../models/job_model.dart';
import '../models/venue_model.dart';
import '../models/hobby_group_model.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8080/api';
  
  Future<List<Product>> getProducts() async {
    try {
      // Geçici olarak direkt product-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String productServiceUrl = 'http://localhost:8082';
      final response = await http.get(
        Uri.parse('$productServiceUrl/products'),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          throw Exception('API request timeout. Check if Product Service is running on http://localhost:8082');
        },
      );

      // Response body kontrolü
      final responseBody = response.body.trim();
      
      // HTML response kontrolü
      if (responseBody.startsWith('<') || responseBody.startsWith('<!DOCTYPE')) {
        throw Exception('API returned HTML instead of JSON. This usually means:\n1. API Gateway is not accessible\n2. CORS issue\n3. Network error\n\nResponse preview: ${responseBody.substring(0, 200)}');
      }

      if (response.statusCode == 200) {
        try {
          final List<dynamic> data = json.decode(responseBody);
          return data.map((json) => Product.fromJson(json)).toList();
        } catch (e) {
          throw Exception('Failed to parse JSON response: $e\nResponse body: ${responseBody.substring(0, 500)}');
        }
      } else {
        throw Exception('Failed to load products: HTTP ${response.statusCode}\nResponse: ${responseBody.substring(0, responseBody.length > 500 ? 500 : responseBody.length)}');
      }
    } on http.ClientException catch (e) {
      throw Exception('Network error: Unable to connect to API Gateway at $baseUrl\n\nPlease check:\n1. API Gateway is running (docker-compose ps)\n2. API Gateway is accessible at http://localhost:8080\n3. No firewall blocking the connection\n\nError: $e');
    } on FormatException catch (e) {
      throw Exception('Invalid JSON response from API.\n\nThis usually means:\n1. API Gateway returned HTML error page\n2. API Gateway is not properly configured\n3. Service is down\n\nError: $e');
    } catch (e) {
      throw Exception('Error fetching products: $e');
    }
  }

  Future<Product> getProductById(String id) async {
    try {
      // Geçici olarak direkt product-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String productServiceUrl = 'http://localhost:8082';
      final response = await http.get(
        Uri.parse('$productServiceUrl/products/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        // Response body'nin JSON olup olmadığını kontrol et
        if (response.body.trim().startsWith('<')) {
          throw Exception('API returned HTML instead of JSON. Check API Gateway configuration.');
        }
        final Map<String, dynamic> data = json.decode(response.body);
        return Product.fromJson(data);
      } else {
        throw Exception('Failed to load product: ${response.statusCode} - ${response.body.substring(0, response.body.length > 200 ? 200 : response.body.length)}');
      }
    } catch (e) {
      if (e is FormatException) {
        throw Exception('Invalid JSON response from API. Check API Gateway and service status.');
      }
      throw Exception('Error fetching product: $e');
    }
  }

  Future<List<Review>> getReviewsByProductId(String productId) async {
    try {
      // Geçici olarak direkt review-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String reviewServiceUrl = 'http://localhost:8087';
      final response = await http.get(
        Uri.parse('$reviewServiceUrl/reviews/product/$productId'),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          throw Exception('API request timeout. Check if Review Service is running on http://localhost:8087');
        },
      );

      // Response body kontrolü
      final responseBody = response.body.trim();
      
      // HTML response kontrolü
      if (responseBody.startsWith('<') || responseBody.startsWith('<!DOCTYPE')) {
        throw Exception('API returned HTML instead of JSON. This usually means:\n1. API Gateway is not accessible\n2. CORS issue\n3. Network error\n\nResponse preview: ${responseBody.substring(0, 200)}');
      }

      if (response.statusCode == 200) {
        try {
          final List<dynamic> data = json.decode(responseBody);
          return data.map((json) => Review.fromJson(json)).toList();
        } catch (e) {
          throw Exception('Failed to parse JSON response: $e\nResponse body: ${responseBody.substring(0, 500)}');
        }
      } else {
        throw Exception('Failed to load reviews: HTTP ${response.statusCode}\nResponse: ${responseBody.substring(0, responseBody.length > 500 ? 500 : responseBody.length)}');
      }
    } on http.ClientException catch (e) {
      throw Exception('Network error: Unable to connect to Review Service at http://localhost:8087\n\nPlease check:\n1. Review Service is running (docker-compose ps)\n2. Review Service is accessible at http://localhost:8087\n3. No firewall blocking the connection\n\nError: $e');
    } on FormatException catch (e) {
      throw Exception('Invalid JSON response from Review Service.\n\nThis usually means:\n1. Review Service returned HTML error page\n2. Review Service is not properly configured\n3. Service is down\n\nError: $e');
    } catch (e) {
      throw Exception('Error fetching reviews: $e');
    }
  }

  Future<RatingSummary> getRatingSummary(String productId) async {
    try {
      // Geçici olarak direkt review-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String reviewServiceUrl = 'http://localhost:8087';
      final response = await http.get(
        Uri.parse('$reviewServiceUrl/reviews/product/$productId/summary'),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          throw Exception('API request timeout. Check if Review Service is running on http://localhost:8087');
        },
      );

      // Response body kontrolü
      final responseBody = response.body.trim();
      
      // HTML response kontrolü
      if (responseBody.startsWith('<') || responseBody.startsWith('<!DOCTYPE')) {
        throw Exception('API returned HTML instead of JSON. This usually means:\n1. API Gateway is not accessible\n2. CORS issue\n3. Network error\n\nResponse preview: ${responseBody.substring(0, 200)}');
      }

      if (response.statusCode == 200) {
        try {
          final Map<String, dynamic> data = json.decode(responseBody);
          return RatingSummary.fromJson(data);
        } catch (e) {
          throw Exception('Failed to parse JSON response: $e\nResponse body: ${responseBody.substring(0, 500)}');
        }
      } else {
        throw Exception('Failed to load rating summary: HTTP ${response.statusCode}\nResponse: ${responseBody.substring(0, responseBody.length > 500 ? 500 : responseBody.length)}');
      }
    } on http.ClientException catch (e) {
      throw Exception('Network error: Unable to connect to Review Service at http://localhost:8087\n\nPlease check:\n1. Review Service is running (docker-compose ps)\n2. Review Service is accessible at http://localhost:8087\n3. No firewall blocking the connection\n\nError: $e');
    } on FormatException catch (e) {
      throw Exception('Invalid JSON response from Review Service.\n\nThis usually means:\n1. Review Service returned HTML error page\n2. Review Service is not properly configured\n3. Service is down\n\nError: $e');
    } catch (e) {
      throw Exception('Error fetching rating summary: $e');
    }
  }

  // Shop Service Methods
  Future<List<ShopModel>> getShops({int page = 0, int size = 20, String? category, String? city, String? search}) async {
    try {
      // Geçici olarak direkt shop-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String shopServiceUrl = 'http://localhost:8092';
      String url = '$shopServiceUrl/shops/active?page=$page&size=$size';
      if (category != null) url = '$shopServiceUrl/shops/active/category/$category?page=$page&size=$size';
      if (city != null) url = '$shopServiceUrl/shops/active/city/$city?page=$page&size=$size';
      if (search != null) url = '$shopServiceUrl/shops/active/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

      final response = await http.get(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final List<dynamic> content = data['content'] ?? data;
        if (kDebugMode) {
          debugPrint('📦 API Response: ${content.length} shops received');
          if (content.isNotEmpty) {
            debugPrint('📦 First shop data: ${content[0]}');
          }
        }
        return content.map((json) => ShopModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load shops: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching shops: $e');
    }
  }

  Future<ShopModel> getShopById(String shopId) async {
    try {
      // Geçici olarak direkt shop-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String shopServiceUrl = 'http://localhost:8092';
      final response = await http.get(
        Uri.parse('$shopServiceUrl/shops/$shopId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        return ShopModel.fromJson(data);
      } else {
        throw Exception('Failed to load shop: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching shop: $e');
    }
  }

  // Jobs Service Methods
  Future<List<JobModel>> getJobs({int page = 0, int size = 20, String? category, String? city, String? jobType, String? search, bool? remote}) async {
    try {
      // Geçici olarak direkt jobs-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String jobsServiceUrl = 'http://localhost:8093';
      String url = '$jobsServiceUrl/jobs/published?page=$page&size=$size';
      if (category != null) url = '$jobsServiceUrl/jobs/published/category/$category?page=$page&size=$size';
      if (city != null) url = '$jobsServiceUrl/jobs/published/city/$city?page=$page&size=$size';
      if (jobType != null) url = '$jobsServiceUrl/jobs/published/job-type/$jobType?page=$page&size=$size';
      if (remote == true) url = '$jobsServiceUrl/jobs/published/remote?page=$page&size=$size';
      if (search != null) url = '$jobsServiceUrl/jobs/published/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

      final response = await http.get(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final List<dynamic> content = data['content'] ?? data;
        if (kDebugMode) {
          debugPrint('📦 API Response (Jobs): ${content.length} jobs received');
          if (content.isNotEmpty) {
            debugPrint('📦 First job data: ${content[0]}');
          }
        }
        return content.map((json) => JobModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load jobs: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching jobs: $e');
    }
  }

  Future<JobModel> getJobById(String jobId) async {
    try {
      // Geçici olarak direkt jobs-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String jobsServiceUrl = 'http://localhost:8093';
      final response = await http.get(
        Uri.parse('$jobsServiceUrl/jobs/$jobId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        return JobModel.fromJson(data);
      } else {
        throw Exception('Failed to load job: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching job: $e');
    }
  }

  // Venues Service Methods
  Future<List<VenueModel>> getVenues({int page = 0, int size = 20, String? venueType, String? city, String? search}) async {
    try {
      // Geçici olarak direkt entertainment-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String entertainmentServiceUrl = 'http://localhost:8095';
      String url = '$entertainmentServiceUrl/venues/active?page=$page&size=$size';
      if (venueType != null) url = '$entertainmentServiceUrl/venues/active/type/$venueType?page=$page&size=$size';
      if (city != null) url = '$entertainmentServiceUrl/venues/active/city/$city?page=$page&size=$size';
      if (search != null) url = '$entertainmentServiceUrl/venues/active/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

      final response = await http.get(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final List<dynamic> content = data['content'] ?? data;
        return content.map((json) => VenueModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load venues: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching venues: $e');
    }
  }

  Future<VenueModel> getVenueById(String venueId) async {
    try {
      // Geçici olarak direkt entertainment-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String entertainmentServiceUrl = 'http://localhost:8095';
      final response = await http.get(
        Uri.parse('$entertainmentServiceUrl/venues/$venueId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        return VenueModel.fromJson(data);
      } else {
        throw Exception('Failed to load venue: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching venue: $e');
    }
  }

  // Hobby Groups Service Methods
  Future<List<HobbyGroupModel>> getHobbyGroups({int page = 0, int size = 20, String? category, String? location, String? search}) async {
    try {
      // Geçici olarak direkt hobby-group-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String hobbyGroupServiceUrl = 'http://localhost:8096';
      String url = '$hobbyGroupServiceUrl/hobby-groups/active?page=$page&size=$size';
      if (category != null) url = '$hobbyGroupServiceUrl/hobby-groups/active/category/$category?page=$page&size=$size';
      if (location != null) url = '$hobbyGroupServiceUrl/hobby-groups/active/location/$location?page=$page&size=$size';
      if (search != null) url = '$hobbyGroupServiceUrl/hobby-groups/active/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

      final response = await http.get(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final List<dynamic> content = data['content'] ?? data;
        if (kDebugMode) {
          debugPrint('📦 API Response (Hobby Groups): ${content.length} groups received');
          if (content.isNotEmpty) {
            debugPrint('📦 First hobby group data: ${content[0]}');
          }
        }
        return content.map((json) => HobbyGroupModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load hobby groups: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching hobby groups: $e');
    }
  }

  Future<HobbyGroupModel> getHobbyGroupById(String groupId) async {
    try {
      // Geçici olarak direkt hobby-group-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String hobbyGroupServiceUrl = 'http://localhost:8096';
      final response = await http.get(
        Uri.parse('$hobbyGroupServiceUrl/hobby-groups/$groupId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        return HobbyGroupModel.fromJson(data);
      } else {
        throw Exception('Failed to load hobby group: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching hobby group: $e');
    }
  }

  // Nearby/Location-based Search Methods
  Future<List<ShopModel>> getNearbyShops(double latitude, double longitude, double radiusKm) async {
    try {
      const String shopServiceUrl = 'http://localhost:8092';
      final response = await http.get(
        Uri.parse('$shopServiceUrl/shops/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => ShopModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load nearby shops: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching nearby shops: $e');
    }
  }

  Future<List<VenueModel>> getNearbyVenues(double latitude, double longitude, double radiusKm) async {
    try {
      const String entertainmentServiceUrl = 'http://localhost:8095';
      final response = await http.get(
        Uri.parse('$entertainmentServiceUrl/venues/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => VenueModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load nearby venues: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching nearby venues: $e');
    }
  }

  Future<List<HobbyGroupModel>> getNearbyHobbyGroups(double latitude, double longitude, double radiusKm) async {
    try {
      const String hobbyGroupServiceUrl = 'http://localhost:8096';
      final response = await http.get(
        Uri.parse('$hobbyGroupServiceUrl/hobby-groups/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => HobbyGroupModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load nearby hobby groups: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching nearby hobby groups: $e');
    }
  }

  Future<List<JobModel>> getNearbyJobs(double latitude, double longitude, double radiusKm) async {
    try {
      const String jobsServiceUrl = 'http://localhost:8093';
      final response = await http.get(
        Uri.parse('$jobsServiceUrl/jobs/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => JobModel.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load nearby jobs: HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching nearby jobs: $e');
    }
  }
}

