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
import 'rum_logger.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8080/api';
  static const String baseUrlV1 = '$baseUrl/v1';
  // Review service direct URL (for rating summary - API Gateway routing issue)
  static const String reviewServiceUrl = 'http://localhost:8087';
  
  Future<List<Product>> getProducts() async {
    final stopwatch = Stopwatch()..start();
    int? status;
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/products'),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          stopwatch.stop();
          RumLogger.logApi(
            name: 'getProducts',
            durationMs: stopwatch.elapsedMilliseconds,
            statusCode: status,
            error: 'timeout',
          );
          throw Exception('API request timeout. Check if Product Service is running on http://localhost:8082');
        },
      );
      status = response.statusCode;

      // Response body kontrolü
      final responseBody = response.body.trim();
      
      // HTML response kontrolü
      if (responseBody.startsWith('<') || responseBody.startsWith('<!DOCTYPE')) {
        throw Exception('API returned HTML instead of JSON. This usually means:\n1. API Gateway is not accessible\n2. CORS issue\n3. Network error\n\nResponse preview: ${responseBody.substring(0, 200)}');
      }

      if (response.statusCode == 200) {
        try {
          final List<dynamic> data = json.decode(responseBody);
          stopwatch.stop();
          RumLogger.logApi(
            name: 'getProducts',
            durationMs: stopwatch.elapsedMilliseconds,
            statusCode: status,
          );
          return data.map((json) => Product.fromJson(json)).toList();
        } catch (e) {
          stopwatch.stop();
          RumLogger.logApi(
            name: 'getProducts',
            durationMs: stopwatch.elapsedMilliseconds,
            statusCode: status,
            error: 'parse:${e.toString()}',
          );
          throw Exception('Failed to parse JSON response: $e\nResponse body: ${responseBody.substring(0, 500)}');
        }
      } else {
        stopwatch.stop();
        RumLogger.logApi(
          name: 'getProducts',
          durationMs: stopwatch.elapsedMilliseconds,
          statusCode: status,
          error: 'http_${response.statusCode}',
        );
        throw Exception('Failed to load products: HTTP ${response.statusCode}\nResponse: ${responseBody.substring(0, responseBody.length > 500 ? 500 : responseBody.length)}');
      }
    } on http.ClientException catch (e) {
      stopwatch.stop();
      RumLogger.logApi(
        name: 'getProducts',
        durationMs: stopwatch.elapsedMilliseconds,
        statusCode: status,
        error: 'client:${e.toString()}',
      );
      throw Exception('Network error: Unable to connect to API Gateway at $baseUrl\n\nPlease check:\n1. API Gateway is running (docker-compose ps)\n2. API Gateway is accessible at http://localhost:8080\n3. No firewall blocking the connection\n\nError: $e');
    } on FormatException catch (e) {
      stopwatch.stop();
      RumLogger.logApi(
        name: 'getProducts',
        durationMs: stopwatch.elapsedMilliseconds,
        statusCode: status,
        error: 'format:${e.toString()}',
      );
      throw Exception('Invalid JSON response from API.\n\nThis usually means:\n1. API Gateway returned HTML error page\n2. API Gateway is not properly configured\n3. Service is down\n\nError: $e');
    } catch (e) {
      stopwatch.stop();
      RumLogger.logApi(
        name: 'getProducts',
        durationMs: stopwatch.elapsedMilliseconds,
        statusCode: status,
        error: e.toString(),
      );
      throw Exception('Error fetching products: $e');
    }
  }

  Future<Product> getProductById(String id) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/products/$id'),
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

  Future<List<Review>> getReviewsByProductId(String productId, {String? userId}) async {
    try {
      // Use direct review-service URL to avoid CORS issues
      // userId parametresi ile kullanıcının beğenme durumu da döner
      final uri = userId != null && userId.isNotEmpty
          ? Uri.parse('$reviewServiceUrl/reviews/product/$productId?userId=$userId')
          : Uri.parse('$reviewServiceUrl/reviews/product/$productId');
      
      final response = await http.get(
        uri,
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
      // Use direct review-service URL due to API Gateway routing issue
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

  /// Mark a review as helpful
  /// userId: The ID of the user marking the review as helpful
  /// Note: In production, this should come from authentication context
  Future<Review> markReviewAsHelpful(String reviewId, String userId) async {
    try {
      final url = '$reviewServiceUrl/reviews/$reviewId/helpful?userId=$userId';
      print('📤 markReviewAsHelpful çağrısı:');
      print('   URL: $url');
      print('   Review ID: $reviewId');
      print('   User ID: $userId');
      
      final response = await http.post(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ).timeout(
        const Duration(seconds: 10),
        onTimeout: () {
          print('❌ API request timeout');
          throw Exception('API request timeout');
        },
      );

      print('📥 Response alındı:');
      print('   Status Code: ${response.statusCode}');
      print('   Body: ${response.body.substring(0, response.body.length > 200 ? 200 : response.body.length)}');

      if (response.statusCode == 200) {
        if (response.body.isEmpty) {
          print('⚠️ Response body boş!');
          throw Exception('Empty response from server');
        }
        final Map<String, dynamic> data = json.decode(response.body);
        final review = Review.fromJson(data);
        print('✅ Review parse edildi: helpfulCount = ${review.helpfulCount}');
        return review;
      } else if (response.statusCode == 409) {
        print('⚠️ 409 Conflict: Already liked');
        throw Exception('You have already marked this review as helpful');
      } else if (response.statusCode == 404) {
        print('⚠️ 404 Not Found: Review not found');
        throw Exception('Review not found');
      } else {
        print('❌ Unexpected status code: ${response.statusCode}');
        print('   Response body: ${response.body}');
        throw Exception('Failed to mark review as helpful: HTTP ${response.statusCode} - ${response.body}');
      }
    } catch (e) {
      print('❌ markReviewAsHelpful hatası: $e');
      throw Exception('Error marking review as helpful: $e');
    }
  }

  // Shop Service Methods
  Future<List<ShopModel>> getShops({int page = 0, int size = 20, String? category, String? city, String? search}) async {
    try {
      String url = '$baseUrlV1/shops/active?page=$page&size=$size';
      if (category != null) url = '$baseUrlV1/shops/active/category/$category?page=$page&size=$size';
      if (city != null) url = '$baseUrlV1/shops/active/city/$city?page=$page&size=$size';
      if (search != null) url = '$baseUrlV1/shops/active/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

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
      final response = await http.get(
        Uri.parse('$baseUrlV1/shops/$shopId'),
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
      String url = '$baseUrlV1/jobs/published?page=$page&size=$size';
      if (category != null) url = '$baseUrlV1/jobs/published/category/$category?page=$page&size=$size';
      if (city != null) url = '$baseUrlV1/jobs/published/city/$city?page=$page&size=$size';
      if (jobType != null) url = '$baseUrlV1/jobs/published/job-type/$jobType?page=$page&size=$size';
      if (remote == true) url = '$baseUrlV1/jobs/published/remote?page=$page&size=$size';
      if (search != null) url = '$baseUrlV1/jobs/published/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

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
      final response = await http.get(
        Uri.parse('$baseUrlV1/jobs/$jobId'),
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
      String url = '$baseUrlV1/venues/active?page=$page&size=$size';
      if (venueType != null) url = '$baseUrlV1/venues/active/type/$venueType?page=$page&size=$size';
      if (city != null) url = '$baseUrlV1/venues/active/city/$city?page=$page&size=$size';
      if (search != null) url = '$baseUrlV1/venues/active/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

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
      final response = await http.get(
        Uri.parse('$baseUrlV1/venues/$venueId'),
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
      String url = '$baseUrlV1/hobby-groups/active?page=$page&size=$size';
      if (category != null) url = '$baseUrlV1/hobby-groups/active/category/$category?page=$page&size=$size';
      if (location != null) url = '$baseUrlV1/hobby-groups/active/location/$location?page=$page&size=$size';
      if (search != null) url = '$baseUrlV1/hobby-groups/active/search?q=${Uri.encodeComponent(search)}&page=$page&size=$size';

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
      final response = await http.get(
        Uri.parse('$baseUrlV1/hobby-groups/$groupId'),
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
      final response = await http.get(
        Uri.parse('$baseUrlV1/shops/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
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
      final response = await http.get(
        Uri.parse('$baseUrlV1/venues/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
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
      final response = await http.get(
        Uri.parse('$baseUrlV1/hobby-groups/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
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
      final response = await http.get(
        Uri.parse('$baseUrlV1/jobs/nearby?latitude=$latitude&longitude=$longitude&radiusKm=$radiusKm'),
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

