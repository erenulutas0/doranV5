import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class OwnProduct {
  final String id;
  final String name;
  final String? description;
  final double price;
  final String category;
  final String status;

  OwnProduct({
    required this.id,
    required this.name,
    this.description,
    required this.price,
    required this.category,
    required this.status,
  });

  factory OwnProduct.fromJson(Map<String, dynamic> json) {
    return OwnProduct(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      description: json['description'],
      price: (json['price'] ?? 0).toDouble(),
      category: json['category'] ?? '',
      status: json['status'] ?? '',
    );
  }
}

class OwnProductsPage extends StatefulWidget {
  const OwnProductsPage({super.key});

  @override
  State<OwnProductsPage> createState() => _OwnProductsPageState();
}

class _OwnProductsPageState extends State<OwnProductsPage> {
  List<OwnProduct> _products = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadProducts();
  }

  Future<void> _loadProducts() async {
    try {
      // Geçici olarak direkt own-product-service'e bağlanıyoruz (API Gateway sorunu çözülene kadar)
      const String ownProductServiceUrl = 'http://localhost:8091';
      final response = await http.get(
        Uri.parse('$ownProductServiceUrl/api/user-products/published?page=0&size=20'),
        headers: {'Accept': 'application/json'},
      ).timeout(const Duration(seconds: 10));

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final List<dynamic> content = data['content'] ?? data;
        setState(() {
          _products = content.map((json) => OwnProduct.fromJson(json)).toList();
          _isLoading = false;
        });
      } else {
        setState(() {
          _error = 'Failed to load products: ${response.statusCode}';
          _isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        _error = 'Error: $e';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    debugPrint('OwnProductsPage build called');
    return Scaffold(
      appBar: AppBar(
        title: const Text('Own Products'),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: () {
              // Navigate to add product
            },
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.error_outline, size: 64, color: Colors.red),
                      const SizedBox(height: 16),
                      Text(_error!, textAlign: TextAlign.center),
                      const SizedBox(height: 16),
                      ElevatedButton(
                        onPressed: _loadProducts,
                        child: const Text('Retry'),
                      ),
                    ],
                  ),
                )
              : _products.isEmpty
                  ? const Center(child: Text('No products found'))
                  : RefreshIndicator(
                      onRefresh: _loadProducts,
                      child: GridView.builder(
                        padding: const EdgeInsets.all(16),
                        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: 2,
                          crossAxisSpacing: 16,
                          mainAxisSpacing: 16,
                          childAspectRatio: 0.75,
                        ),
                        itemCount: _products.length,
                        itemBuilder: (context, index) {
                          final product = _products[index];
                          return Card(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Expanded(
                                  child: Container(
                                    decoration: BoxDecoration(
                                      color: Colors.grey[200],
                                      borderRadius: const BorderRadius.vertical(top: Radius.circular(8)),
                                    ),
                                    child: const Center(
                                      child: Icon(Icons.image, size: 48, color: Colors.grey),
                                    ),
                                  ),
                                ),
                                Padding(
                                  padding: const EdgeInsets.all(8),
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        product.name,
                                        maxLines: 2,
                                        overflow: TextOverflow.ellipsis,
                                        style: const TextStyle(fontWeight: FontWeight.bold),
                                      ),
                                      const SizedBox(height: 4),
                                      Text(
                                        '${product.price.toStringAsFixed(2)} ₺',
                                        style: TextStyle(
                                          color: Theme.of(context).colorScheme.primary,
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
    );
  }
}

