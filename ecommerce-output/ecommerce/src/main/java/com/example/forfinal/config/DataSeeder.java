package com.example.forfinal.config;

import com.example.forfinal.entity.*;
import com.example.forfinal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private ProductImageRepository productImageRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private WishlistItemRepository wishlistItemRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        var existingAdmin = userRepository.findByEmail("admin@megashop.com");
        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@megashop.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setPhone("+1-555-000-0000");
            admin.setFailedAttempts(0);
            admin.setAccountNonLocked(true);
            userRepository.save(admin);
        } else {
            User admin = existingAdmin.get();
            admin.setFailedAttempts(0);
            admin.setAccountNonLocked(true);
            admin.setLockTime(null);
            if (!"ADMIN".equals(admin.getRole())) {
                userRepository.fixAdminRole("admin@megashop.com", passwordEncoder.encode("admin123"));
            }
            userRepository.save(admin);
        }

        var existingTest = userRepository.findByEmail("user@test.com");
        if (existingTest.isEmpty()) {
            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("user@test.com");
            testUser.setPassword(passwordEncoder.encode("user123"));
            testUser.setRole("USER");
            testUser.setPhone("+1-555-111-1111");
            testUser.setFailedAttempts(0);
            testUser.setAccountNonLocked(true);
            userRepository.save(testUser);
        } else {
            User testUser = existingTest.get();
            testUser.setFailedAttempts(0);
            testUser.setAccountNonLocked(true);
            testUser.setLockTime(null);
            userRepository.save(testUser);
        }

        if (productRepository.count() > 0) {
            orderItemRepository.deleteAll();
            wishlistItemRepository.deleteAll();
            cartItemRepository.deleteAll();
            reviewRepository.deleteAll();
            productImageRepository.deleteAll();
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            brandRepository.deleteAll();
        }

        // === Categories ===
        Category electronics = cat("Electronics", "electronics", null);
        Category clothing = cat("Clothing", "clothing", null);
        Category homeKitchen = cat("Home & Kitchen", "home-kitchen", null);
        Category books = cat("Books", "books", null);
        Category sports = cat("Sports & Outdoors", "sports-outdoors", null);
        Category beauty = cat("Beauty & Personal Care", "beauty", null);
        Category toys = cat("Toys & Games", "toys-games", null);
        Category automotive = cat("Automotive", "automotive", null);
        Category petSupplies = cat("Pet Supplies", "pet-supplies", null);
        Category officeSupplies = cat("Office Supplies", "office-supplies", null);
        Category healthHousehold = cat("Health & Household", "health-household", null);
        Category groceries = cat("Grocery & Gourmet Food", "groceries", null);
        Category musicalInstruments = cat("Musical Instruments", "musical-instruments", null);
        Category jewelry = cat("Jewelry", "jewelry", null);
        Category baby = cat("Baby Products", "baby-products", null);
        Category tools = cat("Tools & Home Improvement", "tools", null);

        Category laptops = cat("Laptops", "laptops", electronics);
        Category phones = cat("Phones", "phones", electronics);
        Category audio = cat("Audio", "audio", electronics);
        Category tablets = cat("Tablets", "tablets", electronics);
        Category wearables = cat("Wearables", "wearables", electronics);
        Category gaming = cat("Gaming", "gaming", electronics);
        Category accessories = cat("Accessories", "accessories", electronics);
        Category cameras = cat("Cameras", "cameras", electronics);
        Category networking = cat("Networking", "networking", electronics);
        Category smartHome = cat("Smart Home", "smart-home", electronics);

        Category mens = cat("Men's Clothing", "mens-clothing", clothing);
        Category womens = cat("Women's Clothing", "womens-clothing", clothing);
        Category shoes = cat("Shoes", "shoes", clothing);
        Category jackets = cat("Jackets & Outerwear", "jackets", clothing);
        Category watches = cat("Watches", "watches", clothing);
        Category accessories_clothing = cat("Fashion Accessories", "fashion-accessories", clothing);
        Category swimwear = cat("Swimwear", "swimwear", clothing);
        Category lingerie = cat("Lingerie & Sleepwear", "lingerie", clothing);

        Category appliances = cat("Appliances", "appliances", homeKitchen);
        Category cookware = cat("Cookware", "cookware", homeKitchen);
        Category homeDecor = cat("Home Decor", "home-decor", homeKitchen);
        Category furniture = cat("Furniture", "furniture", homeKitchen);
        Category bedding = cat("Bedding & Bath", "bedding", homeKitchen);
        Category kitchenGadgets = cat("Kitchen Gadgets", "kitchen-gadgets", homeKitchen);

        Category fitness = cat("Fitness", "fitness", sports);
        Category outdoors = cat("Outdoor Gear", "outdoor-gear", sports);
        Category camping = cat("Camping & Hiking", "camping", sports);
        Category cycling = cat("Cycling", "cycling", sports);
        Category waterSports = cat("Water Sports", "water-sports", sports);

        Category skincare = cat("Skincare", "skincare", beauty);
        Category makeup = cat("Makeup", "makeup", beauty);
        Category hairCare = cat("Hair Care", "hair-care", beauty);
        Category fragrance = cat("Fragrance", "fragrance", beauty);

        Category actionFigures = cat("Action Figures", "action-figures", toys);
        Category boardGames = cat("Board Games", "board-games", toys);
        Category buildingToys = cat("Building Toys", "building-toys", toys);
        Category puzzles = cat("Puzzles", "puzzles", toys);

        Category carParts = cat("Car Parts", "car-parts", automotive);
        Category carAccessories = cat("Car Accessories", "car-accessories", automotive);
        Category motorcycle = cat("Motorcycle", "motorcycle", automotive);

        Category dogSupplies = cat("Dog Supplies", "dog-supplies", petSupplies);
        Category catSupplies = cat("Cat Supplies", "cat-supplies", petSupplies);
        Category fishAquatic = cat("Fish & Aquatic", "fish-aquatic", petSupplies);

        Category officeFurniture = cat("Office Furniture", "office-furniture", officeSupplies);
        Category stationery = cat("Stationery", "stationery", officeSupplies);
        Category printers = cat("Printers & Scanners", "printers", officeSupplies);

        Category vitaminsSupplements = cat("Vitamins & Supplements", "vitamins", healthHousehold);
        Category medicalSupplies = cat("Medical Supplies", "medical-supplies", healthHousehold);
        Category householdCleaning = cat("Household Cleaning", "household-cleaning", healthHousehold);

        Category coffeeTea = cat("Coffee & Tea", "coffee-tea", groceries);
        Category snacks = cat("Snacks", "snacks", groceries);
        Category cookingIngredients = cat("Cooking Ingredients", "cooking-ingredients", groceries);

        Category guitars = cat("Guitars", "guitars", musicalInstruments);
        Category keyboards = cat("Keyboards & MIDI", "keyboards", musicalInstruments);
        Category drums = cat("Drums & Percussion", "drums", musicalInstruments);

        Category necklaces = cat("Necklaces", "necklaces", jewelry);
        Category rings = cat("Rings", "rings", jewelry);
        Category earrings = cat("Earrings", "earrings", jewelry);

        Category babyGear = cat("Baby Gear", "baby-gear", baby);
        Category babyClothing = cat("Baby Clothing", "baby-clothing", baby);
        Category nursery = cat("Nursery", "nursery", baby);

        Category powerTools = cat("Power Tools", "power-tools", tools);
        Category handTools = cat("Hand Tools", "hand-tools", tools);
        Category plumbing = cat("Plumbing", "plumbing", tools);

        // === Brands ===
        Brand apple = brand("Apple", "https://picsum.photos/seed/apple/200/200");
        Brand samsung = brand("Samsung", "https://picsum.photos/seed/samsung/200/200");
        Brand google = brand("Google", "https://picsum.photos/seed/google/200/200");
        Brand sony = brand("Sony", "https://picsum.photos/seed/sony/200/200");
        Brand dell = brand("Dell", "https://picsum.photos/seed/dell/200/200");
        Brand lenovo = brand("Lenovo", "https://picsum.photos/seed/lenovo/200/200");
        Brand nintendo = brand("Nintendo", "https://picsum.photos/seed/nintendo/200/200");
        Brand microsoft = brand("Microsoft", "https://picsum.photos/seed/microsoft/200/200");
        Brand logitech = brand("Logitech", "https://picsum.photos/seed/logitech/200/200");
        Brand nike = brand("Nike", "https://picsum.photos/seed/nike/200/200");
        Brand adidas = brand("Adidas", "https://picsum.photos/seed/adidas/200/200");
        Brand levis = brand("Levi's", "https://picsum.photos/seed/levis/200/200");
        Brand northFace = brand("The North Face", "https://picsum.photos/seed/northface/200/200");
        Brand patagonia = brand("Patagonia", "https://picsum.photos/seed/patagonia/200/200");
        Brand kitchenaid = brand("KitchenAid", "https://picsum.photos/seed/kitchenaid/200/200");
        Brand dyson = brand("Dyson", "https://picsum.photos/seed/dyson/200/200");
        Brand vitamix = brand("Vitamix", "https://picsum.photos/seed/vitamix/200/200");
        Brand instantPot = brand("Instant Pot", "https://picsum.photos/seed/instantpot/200/200");
        Brand lego = brand("LEGO", "https://picsum.photos/seed/lego/200/200");
        Brand dysonBeauty = brand("Dyson Beauty", "https://picsum.photos/seed/dysonbeauty/200/200");
        Brand canon = brand("Canon", "https://picsum.photos/seed/canon/200/200");
        Brand nikon = brand("Nikon", "https://picsum.photos/seed/nikon/200/200");
        Brand bosch = brand("Bosch", "https://picsum.photos/seed/bosch/200/200");
        Brand dewalt = brand("DeWalt", "https://picsum.photos/seed/dewalt/200/200");
        Brand makita = brand("Makita", "https://picsum.photos/seed/makita/200/200");
        Brand fender = brand("Fender", "https://picsum.photos/seed/fender/200/200");
        Brand yamaha = brand("Yamaha", "https://picsum.photos/seed/yamaha/200/200");
        Brand seiko = brand("Seiko", "https://picsum.photos/seed/seiko/200/200");
        Brand casio = brand("Casio", "https://picsum.photos/seed/casio/200/200");
        Brand philips = brand("Philips", "https://picsum.photos/seed/philips/200/200");
        Brand braun = brand("Braun", "https://picsum.photos/seed/braun/200/200");
        Brand zebra = brand("Zebra", "https://picsum.photos/seed/zebra/200/200");
        Brand parker = brand("Parker", "https://picsum.photos/seed/parker/200/200");
        Brand yeti = brand("Yeti", "https://picsum.photos/seed/yeti/200/200");
        Brand hydroFlask = brand("Hydro Flask", "https://picsum.photos/seed/hydroflask/200/200");
        Brand orient = brand("Orient", "https://picsum.photos/seed/orient/200/200");
        Brand timex = brand("Timex", "https://picsum.photos/seed/timex/200/200");
        Brand ralphLauren = brand("Ralph Lauren", "https://picsum.photos/seed/ralphlauren/200/200");
        Brand tommyHilfiger = brand("Tommy Hilfiger", "https://picsum.photos/seed/tommy/200/200");
        Brand underArmour = brand("Under Armour", "https://picsum.photos/seed/underarmour/200/200");
        Brand puma = brand("Puma", "https://picsum.photos/seed/puma/200/200");
        Brand reebok = brand("Reebok", "https://picsum.photos/seed/reebok/200/200");
        Brand converse = brand("Converse", "https://picsum.photos/seed/converse/200/200");
        Brand vans = brand("Vans", "https://picsum.photos/seed/vans/200/200");
        Brand hugoBoss = brand("Hugo Boss", "https://picsum.photos/seed/hugoboss/200/200");
        Brand calvinKlein = brand("Calvin Klein", "https://picsum.photos/seed/calvinklein/200/200");
        Brand tissot = brand("Tissot", "https://picsum.photos/seed/tissot/200/200");
        Brand citizen = brand("Citizen", "https://picsum.photos/seed/citizen/200/200");
        Brand shure = brand("Shure", "https://picsum.photos/seed/shure/200/200");
        Brand beyerdynamic = brand("Beyerdynamic", "https://picsum.photos/seed/beyer/200/200");
        Brand steelSeries = brand("SteelSeries", "https://picsum.photos/seed/steelseries/200/200");
        Brand razer = brand("Razer", "https://picsum.photos/seed/razer/200/200");
        Brand hermanMiller = brand("Herman Miller", "https://picsum.photos/seed/hermanmiller/200/200");
        Brand staples = brand("Staples", "https://picsum.photos/seed/staples/200/200");
        Brand pentel = brand("Pentel", "https://picsum.photos/seed/pentel/200/200");
        Brand pilot = brand("Pilot", "https://picsum.photos/seed/pilot/200/200");
        Brand tempurPedic = brand("Tempur-Pedic", "https://picsum.photos/seed/tempur/200/200");
        Brand casper = brand("Casper", "https://picsum.photos/seed/casper/200/200");

        // === PRODUCTS ===

        // --- Laptops ---
        prod("MacBook Pro 16\" M3 Pro", "Apple M3 Pro chip, 18GB RAM, 512GB SSD, 16-inch Liquid Retina XDR display, 22h battery", 2499.99, 1499.99, 15, true, apple, List.of(laptops, electronics));
        prod("MacBook Air M3", "Apple M3 chip, 8GB RAM, 256GB SSD, 13.6-inch Liquid Retina display, Midnight", 1099.99, 899.99, 25, true, apple, List.of(laptops, electronics));
        prod("Dell XPS 15", "Intel Core i7-13700H, 16GB RAM, 512GB SSD, 15.6-inch OLED touchscreen, Platinum Silver", 1799.99, 0, 12, false, dell, List.of(laptops, electronics));
        prod("ThinkPad X1 Carbon Gen 11", "Intel Core i7-1365U, 16GB RAM, 512GB SSD, 14-inch WQHD, lightweight 2.4lbs", 1899.99, 0, 8, false, lenovo, List.of(laptops, electronics));
        prod("ASUS ROG Zephyrus G14", "AMD Ryzen 9, RTX 4070, 16GB RAM, 1TB SSD, 14-inch QHD 165Hz gaming laptop", 1699.99, 0, 10, false, null, List.of(laptops, electronics));
        prod("Microsoft Surface Laptop 5", "Intel Core i5-1235U, 8GB RAM, 256GB SSD, 13.5-inch PixelSense touchscreen", 999.99, 0, 18, false, microsoft, List.of(laptops, electronics));
        prod("HP Spectre x360 16", "Intel Core i7-13700H, 16GB RAM, 512GB SSD, 16-inch 3K+ OLED touch 2-in-1", 1499.99, 0, 7, false, null, List.of(laptops, electronics));

        // --- Phones ---
        prod("iPhone 15 Pro Max", "256GB, Titanium, A17 Pro chip, 48MP camera system, 5x optical zoom", 1199.99, 0, 50, true, apple, List.of(phones, electronics));
        prod("iPhone 15", "128GB, USB-C, 48MP camera, Dynamic Island, 6.1-inch Super Retina XDR", 799.99, 699.99, 60, false, apple, List.of(phones, electronics));
        prod("Samsung Galaxy S24 Ultra", "256GB, Snapdragon 8 Gen 3, S Pen, 200MP camera, Titanium frame", 1299.99, 0, 30, true, samsung, List.of(phones, electronics));
        prod("Google Pixel 8 Pro", "128GB, Tensor G3, 50MP camera, 7 years of updates, 6.7-inch LTPO OLED", 999.99, 0, 20, false, google, List.of(phones, electronics));
        prod("OnePlus 12", "512GB, Snapdragon 8 Gen 3, 50MP Hasselblad, 100W charging, 6.82-inch QHD+", 899.99, 0, 25, false, null, List.of(phones, electronics));
        prod("Samsung Galaxy Z Fold 6", "512GB, 7.6-inch foldable Dynamic AMOLED, Snapdragon 8 Gen 3, 12GB RAM", 1899.99, 0, 8, false, samsung, List.of(phones, electronics));
        prod("Nothing Phone 3", "256GB, Snapdragon 8s Gen 3, 50MP dual camera, Glyph Interface, 6.7-inch OLED", 599.99, 0, 22, false, null, List.of(phones, electronics));

        // --- Audio ---
        prod("Sony WH-1000XM5 Headphones", "Industry-leading noise cancellation, 30h battery, LDAC, multipoint connection", 349.99, 299.99, 40, true, sony, List.of(audio, electronics));
        prod("AirPods Pro 2nd Gen", "Active Noise Cancellation, USB-C, Adaptive Audio, IP54, Find My support", 249.99, 0, 80, true, apple, List.of(audio, electronics));
        prod("Samsung Galaxy Buds3 Pro", "Blade-light design, dual amps, 360 audio, Galaxy AI features", 249.99, 0, 45, false, samsung, List.of(audio, electronics));
        prod("Shure AONIC 50 Gen 2", "Wireless noise-cancelling headphones, 50h battery, high-res audio, adjustable EQ", 349.99, 0, 12, false, shure, List.of(audio, electronics));
        prod("Beyerdynamic DT 900 Pro X", "Open-back studio headphones, STELLAR.45 driver, 48 Ohm, premium velour pads", 299.99, 0, 15, false, beyerdynamic, List.of(audio, electronics));
        prod("Marshall Major V", "Wireless on-ear headphones, 100h battery, foldable design, Bluetooth 5.3", 129.99, 0, 55, false, null, List.of(audio, electronics));
        prod("JBL Charge 5", "Portable Bluetooth speaker, 20h battery, IP67, built-in powerbank, deep bass", 179.99, 0, 60, false, null, List.of(audio, electronics));

        // --- Tablets ---
        prod("iPad Pro 12.9\" M4", "Apple M4 chip, 256GB, Ultra XDR display, Wi-Fi + 5G, Thunderbolt", 1299.99, 0, 18, true, apple, List.of(tablets, electronics));
        prod("iPad Air 11\" M2", "Apple M2 chip, 128GB, Liquid Retina display, 12MP wide camera", 599.99, 0, 22, false, apple, List.of(tablets, electronics));
        prod("Samsung Galaxy Tab S9 Ultra", "14.6-inch Dynamic AMOLED, Snapdragon 8 Gen 2, 256GB, S Pen included", 1199.99, 0, 10, false, samsung, List.of(tablets, electronics));
        prod("Amazon Fire HD 10 Plus", "10.1-inch 1080p display, 32GB, octa-core processor, USB-C, wireless charging", 179.99, 0, 45, false, null, List.of(tablets, electronics));
        prod("Microsoft Surface Pro 10", "Intel Core Ultra 7, 16GB RAM, 256GB SSD, 13-inch PixelSense Flow, Windows 11", 1499.99, 0, 6, false, microsoft, List.of(tablets, electronics));

        // --- Wearables ---
        prod("Apple Watch Ultra 2", "49mm titanium case, Precision dual-frequency GPS, 36h battery, action button", 799.99, 0, 28, true, apple, List.of(wearables, electronics));
        prod("Samsung Galaxy Watch6 Classic", "47mm, rotating bezel, sleep tracking, body composition, Wear OS", 399.99, 0, 35, false, samsung, List.of(wearables, electronics));
        prod("Fitbit Versa 4", "Built-in GPS, 40+ exercise modes, sleep score, 6+ days battery, Google compatibility", 229.99, 0, 40, false, null, List.of(wearables, electronics));
        prod("Garmin Fenix 7X Pro", "51mm, solar charging, multi-band GPS, topo maps, 37 days battery, military grade", 849.99, 0, 9, false, null, List.of(wearables, electronics));
        prod("Whoop 4.0 Band", "Fitness tracker, strain/recovery/sleep monitoring, 5-day battery, waterproof", 239.99, 0, 32, false, null, List.of(wearables, electronics));

        // --- Gaming ---
        prod("Nintendo Switch OLED", "7-inch OLED screen, 64GB internal storage, Neon Blue/Red Joy-Con", 349.99, 0, 25, true, nintendo, List.of(gaming, electronics));
        prod("PlayStation 5 Slim", "825GB SSD, DualSense controller, 4K Blu-ray, WiFi 6, Tempest 3D Audio", 499.99, 0, 15, true, sony, List.of(gaming, electronics));
        prod("Xbox Series X", "1TB SSD, 4K Gaming, 120 FPS, 12 TFLOPS, Quick Resume, backward compatible", 499.99, 449.99, 12, false, microsoft, List.of(gaming, electronics));
        prod("SteelSeries Arctis Nova Pro Wireless", "Premium gaming headset, dual wireless, ANC, 360° spatial audio", 349.99, 0, 20, false, steelSeries, List.of(gaming, electronics));
        prod("Razer DeathAdder V3 Pro", "Wireless gaming mouse, 63g ultra-light, Focus Pro 30K sensor, 90h battery", 149.99, 0, 33, false, razer, List.of(gaming, electronics));
        prod("ASUS ROG Swift PG32UQX", "32-inch 4K Mini-LED, 144Hz, G-Sync Ultimate, HDR1400, 1ms response", 2999.99, 0, 3, false, null, List.of(gaming, electronics));

        // --- Accessories ---
        prod("Logitech MX Master 3S", "Wireless ergonomic mouse, 8K DPI, quiet clicks, USB-C, MagSpeed scroll", 99.99, 0, 55, false, logitech, List.of(accessories, electronics));
        prod("Keychron Q1 Pro", "75% wireless mechanical keyboard, Gateron Jupiter switches, aluminum frame, QMK/VIA", 199.99, 0, 30, false, logitech, List.of(accessories, electronics));
        prod("Samsung 49\" Odyssey OLED G9", "DQHD 5120x1440, 240Hz, 0.03ms, curved gaming monitor, Neo Quantum HDR", 1799.99, 0, 5, false, samsung, List.of(accessories, electronics));
        prod("Dell Ultrasharp 27\" 4K", "U2723QE, IPS Black, USB-C hub, 98% DCI-P3, height adjustable, 4K UHD", 619.99, 0, 14, false, dell, List.of(accessories, electronics));
        prod("Anker PowerCore 26800mAh", "Portable charger, 3 USB ports, 26800mAh capacity, high-speed charging", 65.99, 0, 100, false, null, List.of(accessories, electronics));
        prod("Raspberry Pi 5 8GB", "ARM Cortex-A76, 8GB RAM, dual 4K HDMI, USB 3.0, PCIe 2.0, 40-pin GPIO", 79.99, 0, 42, false, null, List.of(accessories, electronics));

        // --- Cameras ---
        prod("Canon EOS R5", "45MP full-frame mirrorless, 8K video, IBIS, Digic X, RF mount, 12fps", 3899.99, 0, 6, true, canon, List.of(cameras, electronics));
        prod("Nikon Z8", "45.7MP full-frame, 8K/60p video, EXPEED 7, IBIS 6 stops, 120fps burst", 3999.99, 0, 4, false, nikon, List.of(cameras, electronics));
        prod("Sony A7 IV", "33MP full-frame, 4K/60p video, real-time eye AF, 10fps, S-Log3", 2499.99, 0, 11, false, sony, List.of(cameras, electronics));
        prod("GoPro Hero 12 Black", "5.3K video, 27MP photos, HyperSmooth 6.0, waterproof 33ft, HDR", 399.99, 0, 35, false, null, List.of(cameras, electronics));

        // --- Smart Home ---
        prod("Amazon Echo Dot 5th Gen", "Smart speaker with Alexa, improved sound, temperature sensor, smart home hub", 49.99, 0, 85, false, null, List.of(smartHome, electronics));
        prod("Google Nest Hub Max", "10-inch smart display, Google Assistant, video calling, Nest camera, sleep tracking", 229.99, 0, 22, false, google, List.of(smartHome, electronics));
        prod("Philips Hue Starter Kit 4 Bulbs", "White and color ambiance, 4 A19 bulbs + Hue Bridge, smart lighting hub", 199.99, 0, 40, false, null, List.of(smartHome, electronics));
        prod("Ring Video Doorbell Pro 2", "1536p HD, 3D motion detection, bird's eye view, WiFi 6, Alexa integrations", 229.99, 0, 28, false, null, List.of(smartHome, electronics));
        prod("Nest Learning Thermostat 4th Gen", "Auto-schedule, energy saving, remote control, Nest Renew, works with Alexa/Google", 279.99, 0, 15, false, google, List.of(smartHome, electronics));

        // --- Networking ---
        prod("TP-Link Deco XE75 (3-pack)", "Wi-Fi 6E mesh system, 5500 sq ft coverage, 6GHz band, 7 streams, 200+ devices", 299.99, 0, 18, false, null, List.of(networking, electronics));
        prod("ASUS ROG Rapture GT-AXE16000", "Wi-Fi 6E gaming router, quad-band, 16000 Mbps, 2.5G/10G ports, AiMesh", 599.99, 0, 7, false, null, List.of(networking, electronics));

        // === Clothing ===
        prod("Levi's 501 Original Fit Jeans", "Classic straight leg, button fly, 100% cotton denim, authentic since 1873", 79.99, 59.99, 60, true, levis, List.of(mens, clothing));
        prod("Levi's 511 Slim Fit Jeans", "Slim leg, sits at waist, stretch denim, modern fit in dark wash", 69.99, 0, 45, false, levis, List.of(mens, clothing));
        prod("Calvin Klein Modern Fit Suit", "Two-button suit, stretch wool blend, slim notch lapel, flat front pants", 399.99, 0, 12, false, calvinKlein, List.of(mens, clothing));
        prod("Hugo Boss Slim Fit Dress Shirt", "Long sleeve, spread collar, Italian cotton, slim fit in white or blue", 145.00, 0, 28, false, hugoBoss, List.of(mens, clothing));
        prod("Ralph Lauren Oxford Button Down", "Classic fit, long sleeve, button-down collar, oxford cloth, signature embroidered pony", 128.00, 0, 35, false, ralphLauren, List.of(mens, clothing));
        prod("Dockers Men's Chino Pants", "Signature khaki, wrinkle resistant, stain defender, classic fit, multiple colors", 49.99, 0, 80, false, null, List.of(mens, clothing));
        prod("Tommy Hilfiger Crew Neck T-Shirt", "Cotton jersey, classic fit, flag embroidery, available in 15 colors", 34.99, 0, 95, false, tommyHilfiger, List.of(mens, clothing));
        prod("Hanes ComfortBlend Hoodie", "Ultra-soft cotton-poly blend, pullover, fleece-lined, ribbed cuffs and hem", 29.99, 0, 120, false, null, List.of(mens, clothing));
        prod("Dickies 874 Work Pants", "Original fit, 65/35 poly-cotton twill, wrinkle resistant, signature look", 44.99, 0, 85, false, null, List.of(mens, clothing));

        prod("Nike Air Force 1 '07", "Classic white leather sneakers, perforated toe, Nike Air cushioning, rubber sole", 129.99, 0, 45, true, nike, List.of(shoes, clothing));
        prod("Adidas Ultraboost Light", "Lightest Ultraboost ever, adidas PRIMEKNIT+, Continental rubber outsole, boost midsole", 189.99, 0, 38, true, adidas, List.of(shoes, clothing));
        prod("Converse Chuck Taylor All Star", "Classic canvas high top, rubber toe cap, vulcanized sole, heritage style", 69.99, 0, 65, false, converse, List.of(shoes, clothing));
        prod("Vans Old Skool", "Classic low top, suede/canvas, side stripe, waffle rubber outsole, skate shoe", 74.99, 0, 50, false, vans, List.of(shoes, clothing));
        prod("Puma Suede Classic", "Iconic suede sneaker, Formstrip, rubber sole, retro style since 1968", 74.99, 0, 40, false, puma, List.of(shoes, clothing));
        prod("Reebok Club C 85 Vintage", "Classic tennis-inspired, soft leather upper, EVA midsole, terry lining", 89.99, 0, 30, false, reebok, List.of(shoes, clothing));
        prod("Timberland 6-Inch Premium Boot", "Nubuck leather, 400g insulation, seam-sealed waterproof, anti-fatigue technology", 210.00, 0, 22, false, null, List.of(shoes, clothing));

        prod("The North Face 1996 Retro Nuptse Jacket", "700-fill goose down, waterproof, embroidered logo, packable, iconic silhouette", 299.99, 0, 20, true, northFace, List.of(jackets, clothing));
        prod("Patagonia Better Sweater Fleece", "100% recycled polyester fleece, fair trade certified, zip neck, pocket", 149.99, 119.99, 32, false, patagonia, List.of(jackets, clothing));
        prod("Carhartt WIP Detroit Jacket", "Iconic workwear jacket, 12oz firm duck canvas, blanket lined, corduroy collar", 228.00, 0, 18, false, null, List.of(jackets, clothing));
        prod("Canada Goose Chilliwack Bomber", "Arctic Tech fabric, 625-fill duck down, coyote fur ruff, knit cuffs, -25°C rated", 1295.00, 0, 4, false, null, List.of(jackets, clothing));
        prod("Columbia Bugaboo II Fleece Interchange", "3-in-1 ski jacket, waterproof shell + fleece liner, removable hood, Omni-Heat", 199.99, 0, 16, false, null, List.of(jackets, clothing));

        // --- Women's Clothing ---
        prod("Lululemon Align High-Rise Leggings", "Nulu fabric, barely-there feel, high-rise, 25-inch, buttery soft, 4-way stretch", 98.00, 0, 55, true, null, List.of(womens, clothing));
        prod("Nike Dri-FIT One Luxe Leggings", "High-rise, Dri-FIT mesh, side pockets, 7/8 length, tight fit, moisture wicking", 84.99, 0, 38, false, nike, List.of(womens, clothing));
        prod("Free People Stella Lace Bodysuit", "Sheer floral lace, thong back, adjustable straps, delicate scalloped edges", 68.00, 0, 30, false, null, List.of(womens, clothing));
        prod("Zara Faux Leather Pants", "High-waisted, faux leather, slim fit, zip fly, sleek and modern look", 79.90, 0, 25, false, null, List.of(womens, clothing));
        prod("H&M Cashmere Blend Sweater", "Soft cashmere blend, crew neck, long sleeves, relaxed fit, classic wardrobe staple", 69.99, 0, 42, false, null, List.of(womens, clothing));

        // --- Watches ---
        prod("Seiko Prospex SPB143", "Diver's 200m, automatic, 41mm steel, ceramic bezel, 6R35 movement, 70h power", 1250.00, 0, 8, true, seiko, List.of(watches, clothing));
        prod("Casio G-Shock GA2100-1A1", "Carbon core guard, 200m water resistance, shock resistant, 48mm, black", 99.99, 0, 50, false, casio, List.of(watches, clothing));
        prod("Tissot PRX Powermatic 80", "40mm, automatic, blue dial, integrated bracelet, Powermatic 80 movement, 80h", 725.00, 0, 12, false, tissot, List.of(watches, clothing));
        prod("Citizen Eco-Drive Promaster", "Professional diver ISO 6425, 200m, Eco-Drive light powered, 44mm, sapphire", 495.00, 0, 15, false, citizen, List.of(watches, clothing));
        prod("Timex Expedition North Field Post", "41mm, solar charging, 100m water resistance, leather strap, Indiglo light", 179.99, 0, 27, false, timex, List.of(watches, clothing));
        prod("Orient Bambino Small Seconds", "38mm, automatic, cream dial, small seconds subdial, domed crystal, leather strap", 290.00, 0, 20, false, orient, List.of(watches, clothing));

        // --- Fashion Accessories ---
        prod("Ray-Ban Aviator Classic", "Gold frame, green G-15 glass lenses, 100% UV protection, timeless aviator", 163.00, 0, 55, false, null, List.of(accessories_clothing, clothing));
        prod("Ray-Ban Wayfarer Classic", "Black frame, green G-15 lenses, iconic square shape, unisex, UV protection", 153.00, 0, 40, false, null, List.of(accessories_clothing, clothing));
        prod("Oakley Holbrook Sunglasses", "Plutonite lenses, Prizm lens technology, O Matter frame, 100% UV protection", 123.00, 0, 35, false, null, List.of(accessories_clothing, clothing));

        // === Home & Kitchen ===
        prod("KitchenAid Artisan Stand Mixer", "5-quart, 10 speeds, tilt-head design, 250W motor, includes paddle, dough hook, whisk", 449.99, 379.99, 16, true, kitchenaid, List.of(appliances, homeKitchen));
        prod("Vitamix A3500 Blender", "Variable speed, self-cleaning, touchscreen, wireless connectivity, 64oz container", 649.99, 0, 10, true, vitamix, List.of(appliances, homeKitchen));
        prod("Instant Pot Duo Plus 6-Qt", "9-in-1: pressure cook, slow cook, rice cooker, steamer, sauté, yogurt maker, warmer", 89.99, 0, 75, true, instantPot, List.of(appliances, homeKitchen));
        prod("Le Creuset Dutch Oven 5.5qt", "Enameled cast iron, signature Marseille blue, oven safe to 500F, chip resistant", 419.99, 0, 12, false, null, List.of(cookware, homeKitchen));
        prod("Breville Barista Express Impress", "Semi-automatic espresso machine, integrated grinder, assisted tamping, 15 bar pump", 899.99, 0, 8, true, null, List.of(appliances, homeKitchen));
        prod("Dyson V15 Detect Cordless Vacuum", "Laser reveals microscopic dust, 60 minutes run time, LCD screen, whole-machine filtration", 749.99, 0, 14, true, dyson, List.of(appliances, homeKitchen));
        prod("iRobot Roomba j7+", "Self-emptying, PrecisionVision, pet waste avoidance, 3-stage cleaning, maps multiple floors", 599.99, 0, 11, false, null, List.of(appliances, homeKitchen));
        prod("Zwilling Pro 8-inch Chef Knife", "Forged German steel, ergonomic handle, 57 Rockwell hardness, laser-tested edge", 169.99, 0, 22, false, null, List.of(cookware, homeKitchen));
        prod("SodaStream Terra Sparkling Water Maker", "Carbonates water in seconds, BPA-free, includes CO2 cylinder, 1L reusable bottle", 99.99, 0, 50, false, null, List.of(appliances, homeKitchen));

        prod("Tempur-Pedic TEMPUR-Adapt Medium Hybrid", "Queen mattress, 11-inch, cooling cover, TEMPUR material, 10-year warranty", 2199.00, 0, 5, true, tempurPedic, List.of(furniture, homeKitchen));
        prod("Casper Original Mattress", "Queen, 10-inch, airy foam, zoned support, cooling, 100-night trial", 1195.00, 0, 8, false, casper, List.of(furniture, homeKitchen));
        prod("IKEA KALLAX Shelving Unit", "8-cube shelf, 58x58cm, versatile storage, white, particleboard/fiberboard", 89.99, 0, 30, false, null, List.of(furniture, homeKitchen));
        prod("West Elm Mid-Century Coffee Table", "Solid walnut, hairpin legs, spacious surface, minimalist modern design", 399.00, 0, 10, false, null, List.of(furniture, homeKitchen));
        prod("Saarinen Tulip Dining Table", "White marble top, white lacquer base, 47-inch round, iconic modern design by Eero Saarinen", 2595.00, 0, 3, false, null, List.of(furniture, homeKitchen));

        prod("Boll & Branch Signature Hemmed Sheet Set", "Queen, 100% organic cotton, 300 thread count, sateen weave, FSC certified", 249.00, 0, 18, false, null, List.of(bedding, homeKitchen));
        prod("Brooklinen Luxe Core Sheet Set", "Queen, 480 thread count, long-staple cotton, percale weave, wrinkle resistant", 179.00, 0, 25, false, null, List.of(bedding, homeKitchen));
        prod("Buffy Cloud Comforter", "Queen, eucalyptus lyocell fill, cloud-like softness, machine washable, 100% plant-based", 285.00, 0, 14, false, null, List.of(bedding, homeKitchen));

        prod("Philips Hue Starter Kit", "4 white and color ambiance bulbs + Hue Bridge, smart lighting, voice control", 199.99, 0, 40, false, null, List.of(homeDecor, homeKitchen));
        prod("Yankee Candle Large Classic Jar", "25.5oz, 110-150 hour burn, premium soy wax blend, 25+ fragrances available", 29.99, 0, 70, false, null, List.of(homeDecor, homeKitchen));

        // === Books ===
        prod("Atomic Habits by James Clear", "An Easy & Proven Way to Build Good Habits & Break Bad Ones. #1 New York Times bestseller", 16.99, 0, 200, true, null, List.of(books));
        prod("The Pragmatic Programmer 20th Ed", "Timeless tips for software engineers, 2nd edition, revised and updated", 39.99, 0, 65, false, null, List.of(books));
        prod("Clean Code by Robert C. Martin", "A Handbook of Agile Software Craftsmanship, essential for any developer", 37.50, 0, 48, false, null, List.of(books));
        prod("Designing Data-Intensive Applications", "The big ideas behind reliable, scalable, and maintainable systems", 42.99, 0, 38, false, null, List.of(books));
        prod("Sapiens by Yuval Noah Harari", "A Brief History of Humankind. International bestseller, 8M+ copies sold", 15.99, 0, 150, true, null, List.of(books));
        prod("1984 by George Orwell", "Classic dystopian novel, timeless warning about totalitarianism and surveillance", 9.99, 0, 180, false, null, List.of(books));
        prod("The Great Gatsby by F. Scott Fitzgerald", "Jazz Age masterpiece, American dream, lavish parties, and tragic love", 8.99, 0, 95, false, null, List.of(books));
        prod("Thinking, Fast and Slow by Daniel Kahneman", "Nobel Prize winner explores the two systems that drive the way we think", 12.99, 0, 120, false, null, List.of(books));
        prod("The Lean Startup by Eric Ries", "How today's entrepreneurs use continuous innovation to create successful businesses", 14.99, 0, 85, false, null, List.of(books));

        // === Sports & Outdoors ===
        prod("Squat Rack Power Cage", "Adjustable height J-hooks, safety bars, pull-up bar, 1500lb capacity, sturdy steel", 399.99, 0, 7, false, null, List.of(fitness, sports));
        prod("Olympic Weight Set 300lbs", "Cast iron plates, 45lb barbell, collars, 2-inch diameter, includes 300lb set", 499.99, 0, 5, false, null, List.of(fitness, sports));
        prod("Yoga Mat Premium 6mm", "Non-slip TPE, moisture resistant, alignment lines, carrying strap, eco-friendly", 39.99, 0, 90, false, null, List.of(fitness, sports));
        prod("Bowflex SelectTech 552 Dumbbells", "Adjustable 5-52.5 lbs each, replaces 15 pairs, dial system, compact storage", 429.99, 0, 8, false, null, List.of(fitness, sports));
        prod("Peloton Bike+", "Smart indoor cycling, 24-inch rotating screen, WiFi, auto-resistance, premium speakers", 2995.00, 0, 4, true, null, List.of(fitness, sports));
        prod("TRX All-in-One Suspension Trainer", "Full-body strength training, bodyweight exercises, adjustable straps, door anchor included", 149.99, 0, 30, false, null, List.of(fitness, sports));
        prod("Gympro Foam Roller 36-inch", "High-density foam, muscle recovery, deep tissue massage, firm density, blue", 29.99, 0, 55, false, null, List.of(fitness, sports));

        prod("Yeti Rambler 26oz Bottle", "Double-wall vacuum insulated, stainless steel, dishwasher safe, leakproof", 45.00, 0, 110, true, yeti, List.of(outdoors, sports));
        prod("Hydro Flask 32oz Wide Mouth", "TempShield insulation, powder coated, BPA-free, Flex Cap, dishwasher safe", 44.95, 0, 95, false, hydroFlask, List.of(outdoors, sports));

        prod("REI Co-op Half Dome 3 Tent", "3-person, 3-season, waterproof, easy setup, 2 doors/vestibules, 6lb 5oz", 299.00, 0, 10, false, null, List.of(camping, sports));
        prod("Coleman Portable Camping Stove", "2-burner, 20,000 BTU, adjustable controls, wind panels, propane, feeds 4", 89.99, 0, 35, false, null, List.of(camping, sports));
        prod("Osprey Atmos 65 AG Pack", "65-liter, Anti-Gravity suspension, Fit-on-the-fly hipbelt, internal frame, backpacking", 289.00, 0, 8, false, null, List.of(camping, sports));

        prod("Giant Escape 3 Hybrid Bike", "City/commuter, 21-speed, lightweight ALUXX frame, flat handlebar, reliable Shimano", 649.99, 0, 6, false, null, List.of(cycling, sports));
        prod("Trek Marlin 7 Mountain Bike", "29-inch wheels, RockShox fork, 12-speed SRAM, hydraulic disc brakes, Alpha Gold", 1399.99, 0, 4, false, null, List.of(cycling, sports));
        prod("Bell Overdrive MIPS Bike Helmet", "MIPS protection, sweat guide, 15 vents, adjustable visor, lightweight 285g", 74.99, 0, 20, false, null, List.of(cycling, sports));

        prod("Body Glove Performer 11 Paddleboard", "11ft inflatable, 6 inches thick, triple fin, includes pump/paddle/leash", 449.99, 0, 7, false, null, List.of(waterSports, sports));
        prod("Speedo Women's Solid U-Back Swimsuit", "Competition swimsuit, 100% chlorine resistant polyester, flatlock seam", 39.99, 0, 30, false, null, List.of(waterSports, sports));

        // === Beauty & Personal Care ===
        prod("Dyson Airwrap Complete Long", "Styler, Coanda smoothing dryer, 6 attachments, all hair types, multi-styler", 599.99, 0, 9, true, dysonBeauty, List.of(beauty));
        prod("Braun Series 9 Pro Electric Razor", "SmartSync technology, 5-in-1 system, wet & dry, 60 min runtime, charging stand", 269.99, 0, 25, false, braun, List.of(beauty));
        prod("Philips Sonicare DiamondClean 9000", "Smart electric toothbrush, 4 modes, USB charging, pressure sensor, premium travel case", 129.99, 0, 40, false, philips, List.of(beauty));
        prod("The Ordinary Niacinamide 10% + Zinc 1%", "High-strength vitamin and mineral blemish formula, 30ml, reduces shine", 5.99, 0, 300, false, null, List.of(skincare, beauty));
        prod("CeraVe Moisturizing Cream", "Bulk moisturizer for dry skin, hyaluronic acid, 3 essential ceramides, 19oz tub", 19.99, 0, 180, true, null, List.of(skincare, beauty));
        prod("La Roche-Posay Anthelios SPF 50", "Sunscreen fluid, oil free, broad spectrum, antioxidant protection, 1.7oz", 33.99, 0, 65, false, null, List.of(skincare, beauty));
        prod("The Ordinary Hyaluronic Acid 2% + B5", "Hydrating serum, multi-molecular HA, vitamin B5, 30ml, daily hydration", 7.99, 0, 250, false, null, List.of(skincare, beauty));

        prod("Charlotte Tilbury Airbrush Flawless Foundation", "Full coverage, matte finish, SPF 20, 12-hour wear, 44 shades", 48.00, 0, 35, false, null, List.of(makeup, beauty));
        prod("Fenty Beauty Pro Filt'r Soft Matte", "Longwear foundation, 50 shades, soft matte finish, buildable coverage", 39.99, 0, 40, false, null, List.of(makeup, beauty));
        prod("Anastasia Beverly Hills Modern Renaissance Palette", "14-shade eyeshadow palette, warm neutrals, highly pigmented, blendable", 49.00, 0, 25, false, null, List.of(makeup, beauty));

        prod("Olaplex No. 3 Hair Perfector", "At-home bonding treatment, repairs damaged hair, 100ml size, use weekly", 28.00, 0, 80, false, null, List.of(hairCare, beauty));
        prod("GHD Platinum+ Styler", "Professional 1-inch straightener, ultra-zone predictive technology, 365°F", 279.99, 0, 18, false, null, List.of(hairCare, beauty));

        prod("Dior Sauvage Eau de Toilette", "Spicy fresh fragrance, bergamot, Sichuan pepper, ambroxan, 100ml men's cologne", 89.00, 0, 25, false, null, List.of(fragrance, beauty));
        prod("Chanel No. 5 Eau de Parfum", "Iconic floral aldehyde, jasmine, rose, 3.4oz, timeless women's fragrance", 135.00, 0, 15, false, null, List.of(fragrance, beauty));
        prod("Le Labo Santal 33", "Unisex signature scent, sandalwood, leather, violet, 50ml, cult favorite", 285.00, 0, 8, false, null, List.of(fragrance, beauty));

        // === Toys & Games ===
        prod("LEGO Star Wars Millennium Falcon 75192", "Ultimate Collector Series, 7541 pieces, ages 16+, display model", 849.99, 0, 4, true, lego, List.of(buildingToys, toys));
        prod("LEGO Icons Titanic 10294", "Massive 9090-piece build, 53 inches long, ages 18+, museum quality", 679.99, 0, 3, false, lego, List.of(buildingToys, toys));
        prod("LEGO Botanical Succulents 10309", "For adults, 771 pieces, 9 succulents, botanical collection, beautiful display", 59.99, 0, 20, false, lego, List.of(buildingToys, toys));
        prod("Catan Board Game", "Classic strategy game, 3-4 players, ages 10+, 60 min gameplay, expandable", 49.99, 0, 55, false, null, List.of(boardGames, toys));
        prod("Codenames", "Social word game, 4-8 players, ages 14+, 15 min, party favorite", 24.99, 0, 40, false, null, List.of(boardGames, toys));
        prod("Ticket to Ride", "Cross-country train adventure, 2-5 players, ages 8+, 60 min strategy game", 59.99, 0, 30, false, null, List.of(boardGames, toys));
        prod("Jenga Classic", "Wooden stacking blocks game, 54 blocks, ages 6+, 2+ players", 19.99, 0, 70, false, null, List.of(boardGames, toys));
        prod("Rubik's Cube 3x3", "Original classic puzzle, turns smoothly, ages 8+, improves memory and spatial skills", 12.99, 0, 150, false, null, List.of(puzzles, toys));
        prod("Ravensburger Disney 5000 Piece Puzzle", "Disney What-A-Shoppe, 5000 pieces, premium quality, ages 14+", 69.99, 0, 12, false, null, List.of(puzzles, toys));
        prod("Nerf Elite 2.0 Echo Blaster", "Motorized dart blaster, 20 darts, 8-dart clip, up to 80fps, ages 8+", 34.99, 0, 45, false, null, List.of(toys));

        // === Automotive ===
        prod("Michelin Defender 2 Tire - 225/65R17", "All-season touring tire, 80,000 mile warranty, excellent wet traction, quiet ride", 189.99, 0, 20, true, null, List.of(carParts, automotive));
        prod("Bridgestone Blizzak WS90 - 205/55R16", "Winter/snow tire, NanoPro-Tech, 3D sipes, extreme grip in snow and ice", 169.99, 0, 15, false, null, List.of(carParts, automotive));
        prod("Bosch Icon Wiper Blades (26\" + 18\")", "Premium beam blade, dual rubber, built-in spoiler, all-season, 2-pack", 39.99, 0, 80, false, bosch, List.of(carParts, automotive));
        prod("Mobil 1 Extended Performance 5W-30", "Full synthetic motor oil, 20,000 miles protection, 5 quart, API SP approved", 34.99, 0, 100, false, null, List.of(carParts, automotive));
        prod("Optimum No Rinse Wash & Shine", "Waterless car wash, 32oz concentrate, rinseless cleaning, polymer protection", 19.99, 0, 55, false, null, List.of(carAccessories, automotive));
        prod("WeatherTech FloorLiner", "Custom-fit floor mats, laser measured, all-weather protection, easy to clean", 159.99, 0, 12, false, null, List.of(carAccessories, automotive));
        prod("Garmin DriveSmart 65 GPS", "6.95-inch GPS navigator, voice controlled, live traffic, lane guidance, dash cam compatible", 249.99, 0, 10, false, null, List.of(carAccessories, automotive));

        // === Pet Supplies ===
        prod("Purina Pro Plan Adult Dog Food 30lb", "Chicken & rice formula, high protein, probiotics, kibble for active dogs", 54.99, 0, 90, true, null, List.of(dogSupplies, petSupplies));
        prod("Blue Buffalo Wilderness Dog Food 24lb", "Grain-free, high protein, deboned chicken, LifeSource Bits, natural formula", 64.99, 0, 45, false, null, List.of(dogSupplies, petSupplies));
        prod("Kong Classic Dog Toy - Large", "Red rubber chew toy, treat dispensing, durable, natural rubber, for aggressive chewers", 14.99, 0, 120, false, null, List.of(dogSupplies, petSupplies));
        prod("Furbo 360 Dog Camera", "360° rotating, treat tosser, bark alert, 1080p night vision, 2-way audio", 269.99, 0, 8, false, null, List.of(dogSupplies, petSupplies));
        prod("Wild One Dog Leash & Collar Set", "Bold colors, martingale collar, soft yet strong, waterproof, recycled materials", 48.00, 0, 25, false, null, List.of(dogSupplies, petSupplies));

        prod("Purina Fancy Feast Poultry 24-pack", "Canned cat food, grilled salmon and chicken, 3oz each, gourmet wet food", 24.99, 0, 70, false, null, List.of(catSupplies, petSupplies));
        prod("Litter-Robot 4", "Self-cleaning litter box, Whisker app, weight sensor, carbon filter, odor control", 699.00, 0, 5, false, null, List.of(catSupplies, petSupplies));
        prod("Fresh Step Scoopable Litter 25lb", "Clumping cat litter, Febreze freshness, low dust, odor guard, 10-day odor control", 18.99, 0, 85, false, null, List.of(catSupplies, petSupplies));
        prod("Cat Tree Tower 67-inch", "Multi-level condo, sisal scratching posts, hammock, perches, plush toys, beige", 129.99, 0, 12, false, null, List.of(catSupplies, petSupplies));

        prod("Fluval FX6 Canister Filter", "High-performance aquarium filter, 400gph, for up to 400 gal, multi-stage filtration", 299.99, 0, 6, false, null, List.of(fishAquatic, petSupplies));
        prod("API Freshwater Master Test Kit", "550 tests, pH/ammonia/nitrite/nitrate, essential for aquarium monitoring", 34.99, 0, 40, false, null, List.of(fishAquatic, petSupplies));

        // === Office Supplies ===
        prod("Herman Miller Aeron Chair - Size B", "Iconic ergonomic office chair, 8z Pellicle mesh, posture fit, adjustable arms", 1395.00, 0, 5, true, hermanMiller, List.of(officeFurniture, officeSupplies));
        prod("Steelcase Gesture Chair", "Multi-function ergonomic chair, CoreFlex back, 360° arms, adjustable seat depth", 1299.00, 0, 4, false, null, List.of(officeFurniture, officeSupplies));
        prod("Autonomous SmartDesk Core", "Electric standing desk, 53x30, dual motor, height memory, programmable, solid wood", 499.00, 0, 10, false, null, List.of(officeFurniture, officeSupplies));
        prod("Staples Mesh Back Office Chair", "Adjustable height, lumbar support, mesh back, padded seat, budget-friendly", 149.99, 0, 25, false, staples, List.of(officeFurniture, officeSupplies));

        prod("Pentel GraphGear 1000 Mechanical Pencil 0.5mm", "Drafting pencil, metal grip, retractable tip, 4mm guide pipe, professional grade", 12.99, 0, 60, false, pentel, List.of(stationery, officeSupplies));
        prod("Pilot G2 Premium Gel Pens 8-pack", "Smooth writing, medium point 0.7mm, rubber grip, retractable, assorted colors", 14.99, 0, 90, false, pilot, List.of(stationery, officeSupplies));
        prod("Sharpie Permanent Markers 24-pack", "Ultra fine point, assorted colors, waterproof, quick-drying, bold marks", 18.99, 0, 75, false, null, List.of(stationery, officeSupplies));
        prod("Moleskine Classic Notebook - Large", "Hardcover, ruled, 5x8.25, 240 pages, expandable bookmark, elastic closure", 25.99, 0, 50, false, null, List.of(stationery, officeSupplies));
        prod("Post-it Super Sticky Notes 12-pack", "3x3 inch, assorted neon colors, super sticky, recyclable, 12 pads", 21.99, 0, 65, false, null, List.of(stationery, officeSupplies));

        prod("HP LaserJet Pro M404dn", "Monochrome laser printer, 38ppm, duplex, ethernet, 300k duty cycle", 299.99, 0, 8, false, null, List.of(printers, officeSupplies));
        prod("Brother MFC-L2750DW All-in-One", "Monochrome laser, print/copy/scan/fax, duplex, Wi-Fi, auto document feeder", 249.99, 0, 12, false, null, List.of(printers, officeSupplies));

        // === Health & Household ===
        prod("Nature Made Vitamin D3 2000 IU 200ct", "Bone and immune support, high potency, easy swallow, gluten free", 14.99, 0, 120, false, null, List.of(vitaminsSupplements, healthHousehold));
        prod("Centrum Silver Adults 50+ 200ct", "Multivitamin, essential vitamins and minerals, brain/heart/eye health", 19.99, 0, 85, false, null, List.of(vitaminsSupplements, healthHousehold));
        prod("Optimum Nutrition Gold Standard Whey 5lb", "Double rich chocolate, 24g protein per serving, 100% whey, instantized", 74.99, 0, 25, false, null, List.of(vitaminsSupplements, healthHousehold));

        prod("Omron Platinum Blood Pressure Monitor", "Upper arm, 3-user memory, irregular heartbeat detection, clinical validated", 69.99, 0, 30, false, null, List.of(medicalSupplies, healthHousehold));
        prod("Braun ThermoScan 7 Ear Thermometer", "Age adjustable, pre-warmed tip, professional accuracy, color LCD", 49.99, 0, 35, false, braun, List.of(medicalSupplies, healthHousehold));
        prod("N95 Respirator Masks 20-pack", "NIOSH approved, 5-layer protection, fluid resistant, adjustable nose bridge", 29.99, 0, 100, false, null, List.of(medicalSupplies, healthHousehold));

        prod("Clorox Disinfecting Wipes 75ct 3-pack", "Lysol-like, kills 99.9% of germs, bleach-free, citrus scent, 225 wipes total", 14.99, 0, 150, false, null, List.of(householdCleaning, healthHousehold));
        prod("Swiffer Sweeper Wet Mopping Kit", "Starter kit including sweeper, 10 wet pads, 10 dry pads, all-in-one mopping", 19.99, 0, 60, false, null, List.of(householdCleaning, healthHousehold));
        prod("Method All-Purpose Cleaner 28oz", "Eucalyptus mint, plant-based, biodegradable, non-toxic, refillable bottle", 4.99, 0, 110, false, null, List.of(householdCleaning, healthHousehold));

        // === Grocery & Gourmet Food ===
        prod("Philips 3200 Series LatteGo", "Super-automatic espresso machine, silky milk frother, 12 coffee varieties, ceramic grinder", 799.99, 0, 8, false, philips, List.of(coffeeTea, groceries));
        prod("Starbucks Medium Roast Ground Coffee 20oz", "House blend, 100% arabica, medium roast, smooth balanced flavor, 20oz bag", 12.99, 0, 100, false, null, List.of(coffeeTea, groceries));
        prod("Nespresso Vertuo Next Coffee Machine", "Five cup sizes, espresso to carafe, fast heat-up, Wi-Fi, used capsules bin", 179.99, 0, 20, false, null, List.of(coffeeTea, groceries));
        prod("Twinings Earl Grey Tea 100 Bags", "Classic English tea, bergamot flavor, 100 individually wrapped tea bags", 15.99, 0, 80, false, null, List.of(coffeeTea, groceries));
        prod("Yerba Prima Psyllium Husk Powder 24oz", "Daily fiber, organic psyllium, unflavored, mixes with water/juice, 227g", 14.99, 0, 55, false, null, List.of(cookingIngredients, groceries));
        prod("Lundberg Family Farms Organic Rice 4lb", "Long grain brown rice, organic, non-GMO, gluten-free, sustainably grown", 9.99, 0, 60, false, null, List.of(cookingIngredients, groceries));
        prod("Extra Virgin Olive Oil 1 Liter", "Cold pressed, authentic Italian, premium quality, dark glass bottle", 22.99, 0, 45, false, null, List.of(cookingIngredients, groceries));

        // === Musical Instruments ===
        prod("Fender Player Stratocaster HSS", "Electric guitar, alder body, maple neck, 3 single-coils, tremolo, sunburst", 849.99, 0, 6, true, fender, List.of(guitars, musicalInstruments));
        prod("Fender American Professional II Telecaster", "Ash body, Deep C neck, V-Mod II pickups, 3-saddle bridge, natural finish", 1799.99, 0, 3, false, fender, List.of(guitars, musicalInstruments));
        prod("Yamaha FG800 Acoustic Guitar", "Solid spruce top, nato/okume back, rosewood fingerboard, timeless dreadnought", 249.99, 0, 10, false, yamaha, List.of(guitars, musicalInstruments));
        prod("Ibanez GSR200SM Electric Bass", "4-string, Jatoba fingerboard, Dynamix pickups, active EQ, sunburst metallic", 299.99, 0, 8, false, null, List.of(guitars, musicalInstruments));
        prod("Yamaha P-125 Digital Piano", "88-key weighted keys, Pure CF sound, built-in speakers, 24 voices, USB", 699.99, 0, 5, false, yamaha, List.of(keyboards, musicalInstruments));
        prod("Roland TD-17KVX V-Drums", "Electronic drum set, mesh heads, 3-zone ride, 25 kits, Bluetooth, practice/record", 1599.99, 0, 3, false, null, List.of(drums, musicalInstruments));
        prod("Shure SM57 Instrument Microphone", "Dynamic mic, legendary for guitar amps/snares, cardioid, rugged construction", 99.99, 0, 20, false, shure, List.of(musicalInstruments));

        // === Jewelry ===
        prod("Tiffany & Co. Paloma Picasso Olive Leaf Necklace", "18k gold, olive leaf pendant, 16-inch, signature Paloma design", 1750.00, 0, 3, true, null, List.of(necklaces, jewelry));
        prod("Mejuri Classic Tiny Hoops", "14k gold filled, 15mm, polished finish, secure click-lock, everyday earrings", 68.00, 0, 30, false, null, List.of(earrings, jewelry));
        prod("Pandora Moments Sterling Silver Bracelet", "Silver snake chain, threading system, clip charm, smooth, 7.5-inch", 75.00, 0, 40, false, null, List.of(jewelry));
        prod("Cartier Love Ring", "18k yellow gold, classic screw motif, width 2.5mm, iconic stacking ring", 1350.00, 0, 2, false, null, List.of(rings, jewelry));

        // === Baby Products ===
        prod("Baby Brezza Formula Pro Advanced", "Automatic formula maker, 3 settings, 4-second mixing, easy cleaning, water filter", 199.99, 0, 10, false, null, List.of(babyGear, baby));
        prod("Graco Pack 'n Play Playard", "Foldable, bassinet/changing table, newborn to toddler, quick set-up, travel", 149.99, 0, 15, false, null, List.of(babyGear, baby));
        prod("Halo Bassinest Swivel Sleeper", "Bedside bassinet, 360° swivel, adjustable height, breathable mesh", 239.99, 0, 8, false, null, List.of(nursery, baby));
        prod("Ergobaby Omni 360 Baby Carrier", "Newborn to 45lb, 4 positions, ergonomic, lumbar support, breathable mesh", 159.99, 0, 12, false, null, List.of(babyGear, baby));
        prod("BabyBjörn Bouncer Balance Soft", "Ergonomic, 3 recline positions, removable toy bar, organic cotton, toy included", 219.99, 0, 6, false, null, List.of(babyGear, baby));

        // === Tools & Home Improvement ===
        prod("DeWalt 20V Max Cordless Drill Combo Kit", "Drill/driver + impact driver, 2 batteries, charger, bag, brushless motor", 299.99, 0, 12, true, dewalt, List.of(powerTools, tools));
        prod("Makita 18V LXT 6-Piece Combo Kit", "6 tools: drill, impact driver, reciprocating saw, circular saw, 2 batteries", 699.99, 0, 5, false, makita, List.of(powerTools, tools));
        prod("Bosch Router 1617EVSPK", "2.25hp plunge/fixed base, variable speed, soft-start, precision adjustment", 259.99, 0, 7, false, bosch, List.of(powerTools, tools));
        prod("Stanley FatMax 48-22-1110", "10-inch tool box, all-metal latches, deep-well storage, IP54 water resistant", 49.99, 0, 20, false, null, List.of(handTools, tools));
        prod("Stanley 65-Piece Homeowner Tool Kit", "Essential set: hammer, pliers, screwdrivers, wrench, tape measure, level", 39.99, 0, 35, false, null, List.of(handTools, tools));
        prod("Klein Tools 11-in-1 Screwdriver", "Multi-bit, 3 Phillips, 3 slotted, 2 square, 2 Torx, nut driver, heavy duty", 24.99, 0, 45, false, null, List.of(handTools, tools));
        prod("Moen 7597ES Brantford Kitchen Faucet", "One-handle, pull-down sprayer, spot resist stainless, 1.5 GPM, Duralock", 249.99, 0, 10, false, null, List.of(plumbing, tools));
        prod("Kohler K-3946-4Aw Memoirs Toilet", "Elongated comfort height, 1.28 GPF, AquaPiston canister, class five flushing", 399.99, 0, 6, false, null, List.of(plumbing, tools));

        // === Coupons ===
        coupon("WELCOME10", "PERCENTAGE", 10, 50, 100, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(6));
        coupon("SAVE50", "FLAT", 50, 200, 50, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(3));
        coupon("FREESHIP", "PERCENTAGE", 100, 150, 30, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(2));
        coupon("SUMMER25", "PERCENTAGE", 25, 100, 200, LocalDateTime.now(), LocalDateTime.now().plusMonths(4));
        coupon("FLAT100", "FLAT", 100, 500, 20, LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        coupon("MEGA20", "PERCENTAGE", 20, 75, 150, LocalDateTime.now(), LocalDateTime.now().plusMonths(3));
        coupon("VIP50", "FLAT", 50, 150, 100, LocalDateTime.now(), LocalDateTime.now().plusMonths(2));
    }

    private Category cat(String name, String slug, Category parent) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(slug);
        c.setParent(parent);
        return categoryRepository.save(c);
    }

    private Brand brand(String name, String logoUrl) {
        Brand b = new Brand();
        b.setName(name);
        b.setLogoUrl(logoUrl);
        return brandRepository.save(b);
    }

    private void prod(String name, String desc, double price, double discountPrice, int stock, boolean featured, Brand brand, List<Category> categories) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        if (discountPrice > 0) p.setDiscountPrice(discountPrice);
        p.setStock(stock);
        p.setFeatured(featured);
        p.setBrand(brand);
        p.setAverageRating(Math.round((3.5 + Math.random() * 1.5) * 10.0) / 10.0);
        p.setReviewCount((int)(Math.random() * 60) + 3);
        p.setSku("SKU-" + name.substring(0, Math.min(4, name.length())).toUpperCase() + "-" + (int)(Math.random() * 10000));
        for (Category cat : categories) {
            p.getCategories().add(cat);
        }
        Product saved = productRepository.save(p);

        String kw = imageKeywords(name, brand);
        ProductImage img = new ProductImage("https://loremflickr.com/320/320/" + kw, true);
        img.setProduct(saved);
        productImageRepository.save(img);

        if (Math.random() > 0.4) {
            ProductImage img2 = new ProductImage("https://loremflickr.com/320/320/" + kw + ",product", false);
            img2.setProduct(saved);
            productImageRepository.save(img2);
        }
        if (Math.random() > 0.7) {
            ProductImage img3 = new ProductImage("https://loremflickr.com/320/320/" + kw + ",store", false);
            img3.setProduct(saved);
            productImageRepository.save(img3);
        }
    }

    private String imageKeywords(String name, Brand brand) {
        String lower = name.toLowerCase();
        if (lower.contains("macbook")) return "macbook,notebook,apple";
        if (lower.contains("iphone")) return "iphone,smartphone,apple";
        if (lower.contains("ipad")) return "ipad,tablet,apple";
        if (lower.contains("airpods")) return "airpods,earphones,apple";
        if (lower.contains("apple watch")) return "applewatch,smartwatch";
        if (lower.contains("imac")) return "imac,desktop,apple";
        if (lower.contains("samsung galaxy")) return "samsung,smartphone";
        if (lower.contains("pixel")) return "googlepixel,smartphone";
        if (lower.contains("oneplus")) return "oneplus,smartphone";
        if (lower.contains("sony wh")) return "sony,headphones";
        if (lower.contains("sony a7") || lower.contains("sony camera")) return "sony,camera";
        if (lower.contains("canon")) return "canon,camera";
        if (lower.contains("nikon")) return "nikon,camera";
        if (lower.contains("gopro")) return "gopro,camera";
        if (lower.contains("playstation") || lower.contains("ps5")) return "playstation,gaming,console";
        if (lower.contains("xbox")) return "xbox,gaming,console";
        if (lower.contains("nintendo")) return "nintendo,gaming";
        if (lower.contains("kindle")) return "kindle,ebook,amazon";
        if (lower.contains("fitbit") || lower.contains("whoop")) return "fitness,smartwatch";
        if (lower.contains("garmin")) return "garmin,smartwatch";
        if (lower.contains("dell xps") || lower.contains("dell ")) return "dell,notebook";
        if (lower.contains("thinkpad") || lower.contains("lenovo ")) return "lenovo,notebook";
        if (lower.contains("surface")) return "microsoft,notebook";
        if (lower.contains("rog ") || lower.contains("asus")) return "asus,gaming,notebook";
        if (lower.contains("logitech")) return "logitech,keyboard,mouse";
        if (lower.contains("keychron")) return "keyboard,mechanical";
        if (lower.contains("razer")) return "razer,gaming";
        if (lower.contains("steelseries")) return "headphones,gaming";
        if (lower.contains("samsung monitor") || lower.contains("odyssey")) return "monitor,samsung";
        if (lower.contains("ultrasharp") || lower.contains("monitor")) return "monitor,display";
        if (lower.contains("anker")) return "charger,battery";
        if (lower.contains("raspberry")) return "raspberrypi,computer";
        if (lower.contains("airforce") || lower.contains("air force")) return "nike,sneakers";
        if (lower.contains("ultraboost") || lower.contains("adidas ")) return "adidas,sneakers";
        if (lower.contains("converse") || lower.contains("chuck")) return "converse,sneakers";
        if (lower.contains("vans")) return "vans,sneakers";
        if (lower.contains("puma")) return "puma,sneakers";
        if (lower.contains("reebok")) return "reebok,sneakers";
        if (lower.contains("timberland")) return "timberland,boots";
        if (lower.contains("levi") && (lower.contains("501") || lower.contains("jean"))) return "levis,jeans,clothing";
        if (lower.contains("north face") || lower.contains("nuptse")) return "northface,jacket";
        if (lower.contains("patagonia")) return "patagonia,jacket,clothing";
        if (lower.contains("carhartt")) return "carhartt,jacket,clothing";
        if (lower.contains("canada goose")) return "canadagoose,jacket";
        if (lower.contains("ray-ban") || lower.contains("rayban")) return "rayban,sunglasses";
        if (lower.contains("oakley")) return "oakley,sunglasses";
        if (lower.contains("seiko")) return "seiko,watch";
        if (lower.contains("casio") || lower.contains("g-shock")) return "casiowatch,gshock";
        if (lower.contains("tissot")) return "tissot,watch";
        if (lower.contains("citizen")) return "citizen,watch";
        if (lower.contains("timex")) return "timex,watch";
        if (lower.contains("orient")) return "orient,watch";
        if (lower.contains("kitchenaid") || lower.contains("stand mixer")) return "kitchenaid,mixer";
        if (lower.contains("vitamix")) return "vitamix,blender";
        if (lower.contains("instant pot")) return "instantpot,kitchen";
        if (lower.contains("dyson v") || lower.contains("v15 detect")) return "dyson,vacuum";
        if (lower.contains("dyson airwrap") || lower.contains("dyson beauty")) return "dyson,hair";
        if (lower.contains("roomba") || lower.contains("irobot")) return "roomba,vacuum,robot";
        if (lower.contains("le creuset")) return "lecreuset,cookware";
        if (lower.contains("breville")) return "breville,espresso,coffee";
        if (lower.contains("zwilling")) return "zwilling,knife";
        if (lower.contains("sodastream")) return "sodastream";
        if (lower.contains("philips hue")) return "philipshue,lighting";
        if (lower.contains("echo dot") || lower.contains("alexa")) return "alexa,smarthome";
        if (lower.contains("nest")) return "google,nest,smarthome";
        if (lower.contains("ring doorbell")) return "ring,dorbell";
        if (lower.contains("herman miller") || lower.contains("aeron")) return "hermanmiller,chair";
        if (lower.contains("steelcase")) return "steelcase,chair";
        if (lower.contains("tempur") || lower.contains("casper")) return "mattress,bed";
        if (lower.contains("lego") && lower.contains("star wars")) return "lego,starwars";
        if (lower.contains("lego")) return "lego,toys";
        if (lower.contains("catan")) return "catan,boardgame";
        if (lower.contains("codenames")) return "boardgame,party";
        if (lower.contains("ticket to ride")) return "boardgame";
        if (lower.contains("jenga") || lower.contains("rubik")) return "toys,puzzle";
        if (lower.contains("nerf")) return "nerf,toys";
        if (lower.contains("fender")) return "fender,guitar";
        if (lower.contains("yamaha") && lower.contains("guitar")) return "yamaha,guitar";
        if (lower.contains("yamaha") && (lower.contains("piano") || lower.contains("keyboard"))) return "piano,keyboard";
        if (lower.contains("roland")) return "roland,drums";
        if (lower.contains("shure")) return "microphone,shure";
        if (lower.contains("tiffany")) return "tiffany,jewelry";
        if (lower.contains("cartier")) return "cartier,jewelry,ring";
        if (lower.contains("bosch icon") || lower.contains("mobil 1") || lower.contains("michelin")) return "car,tire,automotive";
        if (lower.contains("fluval") || lower.contains("aquarium")) return "aquarium,fish";
        if (lower.contains("purina") || lower.contains("blue buffalo") || lower.contains("fresh step")) return "pet,dog,cat";
        if (lower.contains("litter-robot") || lower.contains("furbo")) return "pet,smart";
        if (lower.contains("konga") || lower.contains("kong ")) return "dog,toy";
        if (lower.contains("dewalt")) return "dewalt,drill,tool";
        if (lower.contains("makita")) return "makita,tool";
        if (lower.contains("klein tools") || lower.contains("stanley")) return "tools";
        if (lower.contains("moen") || lower.contains("kohler")) return "plumbing,faucet";
        if (lower.contains("weights") || lower.contains("squat rack") || lower.contains("olympic")) return "gym,weights,fitness";
        if (lower.contains("bowflex")) return "bowflex,dumbbell";
        if (lower.contains("peloton")) return "peloton,cycling";
        if (lower.contains("trx")) return "fitness,training";
        if (lower.contains("yoga mat")) return "yoga,fitness";
        if (lower.contains("yeti") || lower.contains("hydro flask")) return "bottle,water";
        if (lower.contains("coleman")) return "camping";
        if (lower.contains("osprey") || lower.contains("rei")) return "backpack,camping";
        if (lower.contains("body glove")) return "paddleboard,surf";
        if (lower.contains("speedo")) return "swimsuit,swimming";
        if (lower.contains("giant") || lower.contains("trek")) return "bicycle,cycling";
        if (lower.contains("bell mips")) return "helmet,cycling";
        if (lower.contains("olaplex")) return "hair,beauty";
        if (lower.contains("ghd")) return "hair,styler";
        if (lower.contains("dior") || lower.contains("chanel") || lower.contains("le labo")) return "perfume,fragrance";
        if (lower.contains("charlotte tilbury") || lower.contains("fenty") || lower.contains("anastasia")) return "makeup,cosmetics";
        if (lower.contains("niacinamide") || lower.contains("hyaluronic") || lower.contains("cerave")) return "skincare";
        if (lower.contains("l roche") || lower.contains("la roche")) return "skincare,sunscreen";
        if (lower.contains("braun series")) return "razor,grooming";
        if (lower.contains("sonicare") || lower.contains("philips sonic")) return "toothbrush,oralcare";
        if (lower.contains("baby") || lower.contains("graco") || lower.contains("ergobaby")) return "baby";
        if (lower.contains("pandora")) return "pandora,jewelry,bracelet";
        if (lower.contains("mejuri")) return "jewelry,earrings";
        if (lower.contains("boll and branch") || lower.contains("brooklinen")) return "bedsheet,bedding";
        if (lower.contains("buffy")) return "comforter,bedding";
        if (lower.contains("moleskine")) return "moleskine,notebook";
        if (lower.contains("pilot g2") || lower.contains("sharpie") || lower.contains("post-it")) return "stationery,pen";
        if (lower.contains("pentel")) return "pencil,stationery";
        if (lower.contains("starbucks") || lower.contains("twinings") || lower.contains("nespresso")) return "coffee,tea";

        return lower.replaceAll("[^a-z0-9]", ",").replaceAll(",+", ",").replaceAll("^,|,$", "");
    }

    private void coupon(String code, String type, double value, double minOrder, int maxUsage,
                        LocalDateTime validFrom, LocalDateTime validUntil) {
        if (couponRepository.findByCode(code).isPresent()) return;
        Coupon c = new Coupon();
        c.setCode(code);
        c.setType(type);
        c.setValue(value);
        c.setMinOrderAmount(minOrder);
        c.setMaxUsage(maxUsage);
        c.setCurrentUsage(0);
        c.setActive(true);
        c.setValidFrom(validFrom);
        c.setValidUntil(validUntil);
        couponRepository.save(c);
    }
}
