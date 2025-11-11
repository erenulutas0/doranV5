import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AuthProvider with ChangeNotifier {
  String? _userId;
  String? _userName;
  String? _userEmail;
  bool _isAuthenticated = false;
  bool _isGuestMode = true; // Guest mode varsayılan olarak aktif

  String? get userId => _userId;
  String? get userName => _userName;
  String? get userEmail => _userEmail;
  bool get isAuthenticated => _isAuthenticated;
  bool get isGuestMode => _isGuestMode && !_isAuthenticated;

  // SharedPreferences keys
  static const String _keyIsGuestMode = 'is_guest_mode';
  static const String _keyIsAuthenticated = 'is_authenticated';
  static const String _keyUserId = 'user_id';
  static const String _keyUserName = 'user_name';
  static const String _keyUserEmail = 'user_email';

  AuthProvider() {
    _loadAuthState();
  }

  Future<void> _loadAuthState() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      _isGuestMode = prefs.getBool(_keyIsGuestMode) ?? true;
      _isAuthenticated = prefs.getBool(_keyIsAuthenticated) ?? false;
      _userId = prefs.getString(_keyUserId);
      _userName = prefs.getString(_keyUserName);
      _userEmail = prefs.getString(_keyUserEmail);
      notifyListeners();
    } catch (e) {
      // Hata durumunda guest mode'da başla
      _isGuestMode = true;
      _isAuthenticated = false;
    }
  }

  Future<void> _saveAuthState() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setBool(_keyIsGuestMode, _isGuestMode);
      await prefs.setBool(_keyIsAuthenticated, _isAuthenticated);
      if (_userId != null) await prefs.setString(_keyUserId, _userId!);
      if (_userName != null) await prefs.setString(_keyUserName, _userName!);
      if (_userEmail != null) await prefs.setString(_keyUserEmail, _userEmail!);
    } catch (e) {
      // Hata durumunda devam et
    }
  }

  // Guest mode'u aktif et
  Future<void> enableGuestMode() async {
    _isGuestMode = true;
    _isAuthenticated = false;
    await _saveAuthState();
    notifyListeners();
  }

  // Hızlı giriş metodları
  Future<bool> loginWithGoogle() async {
    // Google Sign-In implementasyonu
    await Future.delayed(const Duration(seconds: 1));
    _isAuthenticated = true;
    _isGuestMode = false;
    _userEmail = 'user@gmail.com';
    _userName = 'Google User';
    _userId = 'google_user_123';
    await _saveAuthState();
    notifyListeners();
    return true;
  }

  Future<bool> loginWithApple() async {
    // Apple Sign-In implementasyonu
    await Future.delayed(const Duration(seconds: 1));
    _isAuthenticated = true;
    _isGuestMode = false;
    _userEmail = 'user@icloud.com';
    _userName = 'Apple User';
    _userId = 'apple_user_123';
    await _saveAuthState();
    notifyListeners();
    return true;
  }

  Future<bool> loginWithSMS(String phoneNumber) async {
    // SMS OTP implementasyonu
    await Future.delayed(const Duration(seconds: 1));
    _isAuthenticated = true;
    _isGuestMode = false;
    _userEmail = '$phoneNumber@sms.com';
    _userName = phoneNumber;
    _userId = 'sms_user_123';
    await _saveAuthState();
    notifyListeners();
    return true;
  }

  Future<bool> login(String email, String password) async {
    // API call to login
    await Future.delayed(const Duration(seconds: 1));
    _isAuthenticated = true;
    _isGuestMode = false;
    _userEmail = email;
    _userName = email.split('@')[0];
    _userId = 'user_${email.hashCode}';
    await _saveAuthState();
    notifyListeners();
    return true;
  }

  Future<bool> register(String name, String email, String password) async {
    // API call to register
    await Future.delayed(const Duration(seconds: 1));
    _isAuthenticated = true;
    _isGuestMode = false;
    _userName = name;
    _userEmail = email;
    _userId = 'user_${email.hashCode}';
    await _saveAuthState();
    notifyListeners();
    return true;
  }

  void logout() {
    _userId = null;
    _userName = null;
    _userEmail = null;
    _isAuthenticated = false;
    _isGuestMode = true; // Logout sonrası guest mode'a dön
    _saveAuthState();
    notifyListeners();
  }

  // Kritik eylem kontrolü (ödeme, favori ekleme, adres kaydetme)
  bool requiresAuthentication() {
    return !_isAuthenticated;
  }
}

