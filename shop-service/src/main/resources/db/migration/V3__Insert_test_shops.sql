-- Shop Service - Test Shops
-- Migration: V3__Insert_test_shops.sql
-- Description: 50+ realistic test shops across Turkey

-- İstanbul - Electronics & Technology
INSERT INTO shops (id, owner_id, name, description, category, address, city, district, postal_code, phone, email, website, latitude, longitude, opening_time, closing_time, working_days, average_rating, review_count, is_active, created_at, updated_at) VALUES
(gen_random_uuid(), gen_random_uuid(), 'TechVision İstanbul', 'En son teknoloji ürünleri ve aksesuarları. Laptop, telefon, tablet satışı ve servisi.', 'Electronics', 'Bağdat Caddesi No:123', 'Istanbul', 'Kadıköy', '34710', '+90 216 555 1234', 'info@techvision.com', 'www.techvision.com', 40.9815, 29.0348, '09:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.5, 127, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Dijital Dünya Beyoğlu', 'Bilgisayar, gaming ekipmanları ve yazılım. Profesyonel danışmanlık hizmeti.', 'Electronics', 'İstiklal Caddesi No:456', 'Istanbul', 'Beyoğlu', '34435', '+90 212 555 2345', 'contact@dijitaldunya.com', 'www.dijitaldunya.com', 41.0369, 28.9784, '10:00', '22:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.7, 203, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Apple Premium Reseller Nişantaşı', 'Yetkili Apple ürünleri satış ve servis merkezi. iPhone, iPad, Mac, Watch.', 'Electronics', 'Nişantaşı Meydanı No:789', 'Istanbul', 'Şişli', '34365', '+90 212 555 3456', 'nisantasi@applereseller.com', 'www.applereseller.com.tr', 41.0465, 28.9942, '10:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.8, 312, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- İstanbul - Fashion
(gen_random_uuid(), gen_random_uuid(), 'Moda Butik Osmanbey', 'Kadın ve erkek giyim. İtalyan tasarım, premium kalite.', 'Fashion', 'Halaskargazi Caddesi No:234', 'Istanbul', 'Osmanbey', '34371', '+90 212 555 4567', 'info@modabutik.com', 'www.modabutik.com', 41.0498, 28.9872, '10:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.3, 89, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Vintage Atelier Galata', 'Özel tasarım vintage kıyafetler ve aksesuarlar. Retro tarz uzmanlığı.', 'Fashion', 'Galata Kulesi Sokak No:12', 'Istanbul', 'Beyoğlu', '34421', '+90 212 555 5678', 'contact@vintageatelier.com', 'www.vintageatelier.com', 41.0259, 28.9742, '11:00', '21:00', '["Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.6, 145, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Elit Moda Bağdat Caddesi', 'Dünya markaları ve lüks giyim. Kişisel stil danışmanlığı.', 'Fashion', 'Bağdat Caddesi No:567', 'Istanbul', 'Kadıköy', '34730', '+90 216 555 6789', 'info@elitmoda.com', 'www.elitmoda.com.tr', 40.9776, 29.0521, '10:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.7, 234, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- İstanbul - Food & Grocery
(gen_random_uuid(), gen_random_uuid(), 'Organik Market Beşiktaş', 'Taze organik meyve, sebze ve gıda ürünleri. Çiftlikten sofraya.', 'Food', 'Çarşı Meydanı No:45', 'Istanbul', 'Beşiktaş', '34353', '+90 212 555 7890', 'info@organikmarket.com', 'www.organikmarket.com.tr', 41.0422, 29.0067, '08:00', '22:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.4, 178, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Gurme Şarküteri Etiler', 'İthal peynirler, şaraplar ve gurme ürünler. Premium lezzetler.', 'Food', 'Nispetiye Caddesi No:123', 'Istanbul', 'Beşiktaş', '34337', '+90 212 555 8901', 'contact@gurmesarkuteri.com', 'www.gurmesarkuteri.com', 41.0745, 29.0189, '09:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.8, 267, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Balık Evi Kumkapı', 'Taze deniz ürünleri ve balıklar. Sabah avlanan, akşam sofranızda.', 'Food', 'Kumkapı İskele Sokak No:78', 'Istanbul', 'Fatih', '34130', '+90 212 555 9012', 'info@balikevi.com', 'www.balikevi.com.tr', 41.0026, 28.9541, '07:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.5, 192, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Ankara - Electronics
(gen_random_uuid(), gen_random_uuid(), 'Teknoloji Merkezi Kızılay', 'Bilgisayar, telefon, elektronik aksesuar. Öğrenci indirimleri.', 'Electronics', 'Atatürk Bulvarı No:234', 'Ankara', 'Çankaya', '06420', '+90 312 555 1111', 'info@teknolojimerke

zi.com', 'www.teknolojimerke

zi.com', 39.9189, 32.8537, '09:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.3, 156, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'GameZone Ankara', 'Oyun konsolları, PC gaming, VR ekipmanları. E-sports turnuva merkezi.', 'Electronics', 'Tunalı Hilmi Caddesi No:67', 'Ankara', 'Çankaya', '06680', '+90 312 555 2222', 'info@gamezone.com.tr', 'www.gamezone.com.tr', 39.9108, 32.8561, '10:00', '22:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.6, 234, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Ankara - Fashion
(gen_random_uuid(), gen_random_uuid(), 'Şık Butik Ulus', 'Kadın giyim ve aksesuarlar. Uygun fiyat, kaliteli kumaş.', 'Fashion', 'Anafartalar Caddesi No:123', 'Ankara', 'Ulus', '06050', '+90 312 555 3333', 'contact@sikbutik.com', 'www.sikbutik.com.tr', 39.9447, 32.8621, '10:00', '19:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.2, 98, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Erkek Moda Bahçelievler', 'Erkek takım elbise, gömlek, ayakkabı. Düğün ve özel günler.', 'Fashion', 'Bahçelievler Caddesi No:456', 'Ankara', 'Çankaya', '06490', '+90 312 555 4444', 'info@erkekmoda.com', 'www.erkekmoda.com.tr', 39.9047, 32.8325, '10:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.4, 112, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- İzmir - Electronics
(gen_random_uuid(), gen_random_uuid(), 'Teknosa Alsancak', 'Elektronik ürünler, beyaz eşya, küçük ev aletleri.', 'Electronics', '1469 Sokak No:12', 'Izmir', 'Konak', '35220', '+90 232 555 5555', 'alsancak@teknosa.com', 'www.teknosa.com.tr', 38.4381, 27.1438, '10:00', '22:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.5, 289, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Drone Dünyası İzmir', 'Drone satışı, kurs ve kiralama. Hava fotoğrafçılığı ekipmanları.', 'Electronics', 'Cumhuriyet Bulvarı No:345', 'Izmir', 'Alsancak', '35220', '+90 232 555 6666', 'info@dronedunyasi.com', 'www.dronedunyasi.com.tr', 38.4348, 27.1426, '10:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.7, 167, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- İzmir - Fashion
(gen_random_uuid(), gen_random_uuid(), 'Ege Moda Karşıyaka', 'Plaj giyim, yazlık kıyafetler. Rengarenk yaz koleksiyonu.', 'Fashion', 'Kordon Boyu No:789', 'Izmir', 'Karşıyaka', '35540', '+90 232 555 7777', 'info@egemoda.com', 'www.egemoda.com.tr', 38.4596, 27.1240, '10:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.3, 201, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Deri Atölyesi Çeşme', 'El yapımı deri çanta, cüzdan, kemer. Özel tasarım.', 'Fashion', 'Çeşme Marina No:23', 'Izmir', 'Çeşme', '35930', '+90 232 555 8888', 'contact@deriatölyesi.com', 'www.deriatölyesi.com.tr', 38.3229, 26.3056, '10:00', '22:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.8, 143, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Antalya - Electronics
(gen_random_uuid(), gen_random_uuid(), 'Dijital Plaza Lara', 'Tatil fotoğrafçılığı ekipmanları. Kamera, gimbal, su altı kameraları.', 'Electronics', 'Lara Sahil Yolu No:456', 'Antalya', 'Muratpaşa', '07230', '+90 242 555 9999', 'info@dijitalplaza.com', 'www.dijitalplaza.com.tr', 36.8569, 30.7483, '09:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.5, 178, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Antalya - Fashion  
(gen_random_uuid(), gen_random_uuid(), 'Tatil Moda Kaleiçi', 'Tatil kıyafetleri, mayo, bikini, pareo. Yazlık şıklığı.', 'Fashion', 'Kaleiçi Yat Limanı No:34', 'Antalya', 'Muratpaşa', '07100', '+90 242 555 0000', 'info@tatilmoda.com', 'www.tatilmoda.com.tr', 36.8851, 30.7048, '10:00', '23:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.4, 267, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bursa - Electronics
(gen_random_uuid(), gen_random_uuid(), 'Elektronik Çarşısı Osmangazi', 'Uygun fiyat elektronik. Toptan ve perakende satış.', 'Electronics', 'Atatürk Caddesi No:567', 'Bursa', 'Osmangazi', '16200', '+90 224 555 1212', 'info@elektronikcarsisi.com', 'www.elektronikcarsisi.com.tr', 40.1826, 29.0655, '09:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.2, 134, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bursa - Fashion
(gen_random_uuid(), gen_random_uuid(), 'İpek Yolu Butik', 'Bursa ipeği ve ipekli kumaşlar. Geleneksel ve modern tasarım.', 'Fashion', 'Koza Han No:12', 'Bursa', 'Osmangazi', '16010', '+90 224 555 2323', 'info@ipekyolu.com', 'www.ipekyolu.com.tr', 40.1834, 29.0626, '09:00', '19:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.7, 198, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Adana - Electronics
(gen_random_uuid(), gen_random_uuid(), 'TeknoMart Adana', 'Klima, vantilatör, serinletici cihazlar. Yaz sıcağına çözüm.', 'Electronics', 'İnönü Caddesi No:234', 'Adana', 'Seyhan', '01120', '+90 322 555 3434', 'info@teknomart.com', 'www.teknomart.com.tr', 36.9915, 35.3305, '08:30', '20:30', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.3, 156, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Adana - Food
(gen_random_uuid(), gen_random_uuid(), 'Adana Lezzet Durağı', 'Adana kebap baharatları, Türk şarküteri ürünleri.', 'Food', 'Ziyapaşa Bulvarı No:345', 'Adana', 'Seyhan', '01140', '+90 322 555 4545', 'info@adanalezzet.com', 'www.adanalezzet.com.tr', 37.0012, 35.3216, '08:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.6, 223, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Gaziantep - Food
(gen_random_uuid(), gen_random_uuid(), 'Antep Baklavacısı', 'El yapımı baklava, katmer, künefe. 3 kuşaktır usta işi.', 'Food', 'Gaziler Caddesi No:123', 'Gaziantep', 'Şahinbey', '27010', '+90 342 555 5656', 'info@antepbaklavacisi.com', 'www.antepbaklavacisi.com.tr', 37.0662, 37.3781, '07:00', '23:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.9, 543, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), gen_random_uuid(), 'Gaziantep Kuruyemiş Sarayı', 'Antep fıstığı, badem, ceviz. Premium kuruyemiş çeşitleri.', 'Food', 'Hürriyet Caddesi No:456', 'Gaziantep', 'Şehitkamil', '27500', '+90 342 555 6767', 'info@kuruyemissarayi.com', 'www.kuruyemissarayi.com.tr', 37.0594, 37.3825, '08:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.7, 387, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Trabzon - Food
(gen_random_uuid(), gen_random_uuid(), 'Karadeniz Lezzetleri', 'Hamsi, mısır unu, Trabzon ekmeği. Bölgesel tatlar.', 'Food', 'Meydan Parkı Caddesi No:78', 'Trabzon', 'Ortahisar', '61040', '+90 462 555 7878', 'info@karadenizlezzetleri.com', 'www.karadenizlezzetleri.com.tr', 40.9983, 39.7262, '08:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.5, 234, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Konya - Home & Furniture
(gen_random_uuid(), gen_random_uuid(), 'Selçuklu Mobilya', 'Klasik ve modern mobilya. Ev ve ofis için çözümler.', 'Home', 'Mevlana Caddesi No:234', 'Konya', 'Meram', '42030', '+90 332 555 8989', 'info@selcuklumobilya.com', 'www.selcuklumobilya.com.tr', 37.8670, 32.4819, '09:00', '19:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.4, 189, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Diyarbakır - Fashion
(gen_random_uuid(), gen_random_uuid(), 'Mezopotamya El Sanatları', 'Geleneksel Diyarbakır işi takılar, kemerler, çantalar.', 'Fashion', 'Suriçi Hasan Paşa Hanı No:12', 'Diyarbakır', 'Sur', '21100', '+90 412 555 9090', 'info@mezopotamyaelsanatlari.com', 'www.mezopotamyaelsanatlari.com.tr', 37.9108, 40.2359, '09:00', '19:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.6, 156, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Eskişehir - Electronics
(gen_random_uuid(), gen_random_uuid(), 'Bilim Teknoloji Eskişehir', '3D yazıcılar, robotik kitleri, maker ekipmanları. Eğitim odaklı.', 'Electronics', 'Atatürk Bulvarı No:345', 'Eskişehir', 'Odunpazarı', '26030', '+90 222 555 0101', 'info@bilimteknoloji.com', 'www.bilimteknoloji.com.tr', 39.7667, 30.5256, '10:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.8, 267, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Samsun - Food
(gen_random_uuid(), gen_random_uuid(), 'Samsun Balıkçısı', 'Hamsi, çinekop, mezgit. Taze Karadeniz balıkları.', 'Food', 'Liman Caddesi No:567', 'Samsun', 'İlkadım', '55100', '+90 362 555 1122', 'info@samsunbalikci.com', 'www.samsunbalikci.com.tr', 41.2867, 36.3300, '07:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.4, 198, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Kayseri - Fashion
(gen_random_uuid(), gen_random_uuid(), 'Erciyes Moda', 'Kayak kıyafetleri, sporcu giyim. Kış sporları ekipmanları.', 'Fashion', 'Kılıçarslan Caddesi No:123', 'Kayseri', 'Melikgazi', '38010', '+90 352 555 2233', 'info@erciyesmoda.com', 'www.erciyesmoda.com.tr', 38.7205, 35.4826, '09:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.3, 145, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Mersin - Food
(gen_random_uuid(), gen_random_uuid(), 'Akdeniz Zeytin Evi', 'Zeytinyağı, salamura zeytin, domates kurusu. Akdeniz lezzetleri.', 'Food', 'Sahil Yolu No:234', 'Mersin', 'Mezitli', '33330', '+90 324 555 3344', 'info@akdenizzeytinevi.com', 'www.akdenizzeytinevi.com.tr', 36.7506, 34.5583, '08:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.5, 212, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Denizli - Home
(gen_random_uuid(), gen_random_uuid(), 'Pamukkale Tekstil Evi', 'Ev tekstili, havlu, bornoz, yatak örtüsü. Pamuk kalitesi.', 'Home', 'Çamlık Caddesi No:456', 'Denizli', 'Pamukkale', '20160', '+90 258 555 4455', 'info@pamukkaletekstil.com', 'www.pamukkaletekstil.com.tr', 37.7742, 29.0875, '09:00', '19:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.6, 234, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Malatya - Food
(gen_random_uuid(), gen_random_uuid(), 'Malatya Kayısı Bahçesi', 'Organik kuru kayısı, kayısı reçeli, pestili. Doğal tatlar.', 'Food', 'Fuzuli Caddesi No:789', 'Malatya', 'Yeşilyurt', '44920', '+90 422 555 5566', 'info@malatyakayisi.com', 'www.malatyakayisi.com.tr', 38.3554, 38.3095, '08:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.8, 456, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Şanlıurfa - Food
(gen_random_uuid(), gen_random_uuid(), 'Urfa Çiğ Köfte Evi', 'Geleneksel çiğ köfte, isot, Urfa kebabı baharatları.', 'Food', 'Sarayönü Caddesi No:123', 'Şanlıurfa', 'Eyyübiye', '63320', '+90 414 555 6677', 'info@urfaciğkoftevi.com', 'www.urfaciğkoftevi.com.tr', 37.1591, 38.7969, '07:00', '23:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.7, 378, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Van - Food
(gen_random_uuid(), gen_random_uuid(), 'Van Kahvaltı Sofrası', 'Van otlu peyniri, bal, kaymak, reçeller. Doğu kahvaltı lezzetleri.', 'Food', 'Cumhuriyet Caddesi No:234', 'Van', 'İpekyolu', '65100', '+90 432 555 7788', 'info@vankahvaltı.com', 'www.vankahvaltı.com.tr', 38.4891, 43.4089, '06:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.9, 512, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Çanakkale - Fashion
(gen_random_uuid(), gen_random_uuid(), 'Truva Hediyelik Eşya', 'El yapımı seramik, çömlekler, hediyelik eşyalar.', 'Fashion', 'Atatürk Caddesi No:345', 'Çanakkale', 'Merkez', '17100', '+90 286 555 8899', 'info@truvahediyelik.com', 'www.truvahediyelik.com.tr', 40.1553, 26.4142, '09:00', '21:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.4, 189, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bodrum - Fashion
(gen_random_uuid(), gen_random_uuid(), 'Bodrum Marina Boutique', 'Lüks yat giyim, denizci tarzı kıyafetler. Premium marka temsilcisi.', 'Fashion', 'Marina Yat Kulübü No:12', 'Muğla', 'Bodrum', '48400', '+90 252 555 9900', 'info@bodrummarina.com', 'www.bodrummarina.com.tr', 37.0342, 27.4305, '10:00', '23:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.8, 378, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Fethiye - Electronics
(gen_random_uuid(), gen_random_uuid(), 'Fethiye Tekne Elektroniği', 'Tekne GPS, balık bulucu, deniz radar sistemleri.', 'Electronics', 'Liman Caddesi No:67', 'Muğla', 'Fethiye', '48300', '+90 252 555 0011', 'info@fethiyeelektronik.com', 'www.fethiyeelektronik.com.tr', 36.6213, 29.1142, '09:00', '19:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.5, 123, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Kütahya - Home
(gen_random_uuid(), gen_random_uuid(), 'Kütahya Porselen Sarayı', 'El yapımı çini ve porselen ürünler. Kütahya işi özel tasarım.', 'Home', 'Çarşı Meydanı No:45', 'Kütahya', 'Merkez', '43030', '+90 274 555 1133', 'info@kutahyaporselen.com', 'www.kutahyaporselen.com.tr', 39.4242, 29.9833, '09:00', '19:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.7, 267, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Aydın - Food
(gen_random_uuid(), gen_random_uuid(), 'Aydın İncir Bahçesi', 'Kuru incir, incir reçeli, incir ezmesi. Organik üretim.', 'Food', 'Atatürk Bulvarı No:456', 'Aydın', 'Efeler', '09010', '+90 256 555 2244', 'info@aydinincir.com', 'www.aydinincir.com.tr', 37.8480, 27.8436, '08:00', '20:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"]', 4.6, 298, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bolu - Food
(gen_random_uuid(), gen_random_uuid(), 'Bolu Köroğlu Restaurant', 'Mengen yöresel yemekleri, aşçı okulu mezunları tarafından.', 'Food', 'İzzet Baysal Caddesi No:123', 'Bolu', 'Merkez', '14100', '+90 374 555 3355', 'info@koro

glurestaurant.com', 'www.koro

glurestaurant.com.tr', 40.7394, 31.6061, '08:00', '22:00', '["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]', 4.8, 423, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
