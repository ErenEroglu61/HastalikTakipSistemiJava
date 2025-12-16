# Hastalık Takip Sistemi (Java)

## Katkıda Bulunanlar
- Eren EROĞLU — 170424035  
- Emre OKÇELEN — 170421929  
- Fatma TANRIVERDİ — 170424023  
- Engin ÇETİNTAŞ — 170422026  

## Proje Özeti
Bu proje, doktor ve hastaların hastalık seyirini takip edebildiği basit bir Java uygulamasıdır. Amaç; hasta tahlil verilerinin güvenli bir şekilde kaydedilmesi, doktorun bu verileri görmesi ve "Hastalarım" görünümünde hasta listesi veya form şeklinde gösterilebilmesidir. Veri kalıcı depolama olarak JSON dosyası kullanılır.

## Temel Özellikler
- Doctor ve Hasta için ayrı giriş ekranları (login).
- Hasta hesabı üzerinden tahlil (laboratuvar) verilerinin girilmesi.
- Girilen tahlillerin JSON formatında dosyaya kaydedilmesi.
- Doktorun ilgili hastanın tahlillerini görüntüleyebilmesi.
- "Hastalarım" bölümünde:
  - Liste görünümü: Hastaların kısa bilgileri ve son tahlil özetleri.
  - Form/genişletilmiş görünüm: Bir hastaya tıklandığında detaylı tahlil formu gösterilir.
- Basit doğrulama ve hata yönetimi (ör. eksik alan, bozuk JSON).

## Örnek JSON Yapısı
Aşağıdaki örnek, her hasta için saklanabilecek JSON formatını gösterir:

{
  "patients": [
    {
      "id": "170424035",
      "name": "Eren EROĞLU",
      "birthDate": "1997-05-10",
      "gender": "Erkek",
      "tests": [
        {
          "date": "2025-12-01",
          "type": "Kan Tahlili",
          "results": {
            "hemoglobin": 14.2,
            "wbc": 6200
          },
          "notes": "Normal aralıkta"
        }
      ]
    }
  ],
  "doctors": [
    {
      "id": "dr001",
      "name": "Dr. Ahmet Örnek",
      "specialty": "Genel"
    }
  ]
}

(Not: Gerçek dosyada geçerli JSON formatı için tek tırnak değil çift tırnak kullanılmalıdır.)

## Kullanılan / Önerilen Java Özellikleri ve Nasıl Kullanılırlar
Aşağıda projede kullanılması önerilen Java özellikleri ve rolleri bulunmaktadır:

- Nesne Yönelimli Programlama (OOP)
  - Sınıflar: Patient, Doctor, TestRecord, TestResult, AuthService vb.
  - Soyutlama ve encapsulation ile veri modellerinin yönetimi.
- Koleksiyonlar (java.util)
  - List, Map gibi yapıların hasta, doktor ve tahlil kayıtlarını tutmak için kullanımı.
- JSON Okuma/Yazma
  - Gson veya Jackson kütüphanesi ile nesne <-> JSON dönüşümleri.
  - Örnek: Gson gson = new Gson(); gson.toJson(patientList); Files.write(...);
- Dosya I/O (java.nio.file)
  - JSON dosyasını okuma/yazma için Files.readString / Files.write.
- Stream API ve Lambda (java.util.stream)
  - Hasta filtreleme, sıralama, son tahlili alma gibi işlemler için.
- Exception Handling
  - Bozuk JSON, I/O hataları veya doğrulama hatalarını yakalamak için try-catch blokları.
- Generics ve Optional
  - Tip güvenliği ve null-safety için.
- Basit Çok Katmanlı Mimari (Layered / MVC yaklaşımları)
  - Model (veri sınıfları), Service (iş kuralları, I/O), UI (girişler / görüntüleme).
- (Opsiyonel) Concurrency
  - Aynı anda birden fazla yazma işlemi olursa senkronizasyon veya basit kilitleme stratejileri.
- (UI için seçenekler)
  - Masaüstü: JavaFX veya Swing — giriş formları ve "Hastalarım" arayüzü için.
  - Web: Spring Boot + Thymeleaf veya REST + JS frontend — daha ölçeklenebilir bir yapı istenirse.

## Proje Yapısı (Öneri)
- src/
  - com.follow_disease.com.follow_disease.controller.model/ (Patient, Doctor, TestRecord)
  - service/ (AuthService, PatientService, JsonStorageService)
  - ui/ (Swing/JavaFX veya web com.follow_disease.com.follow_disease.controller)
  - util/ (JsonUtils, ValidationUtils)
- data/
  - patients.json (uygulama çalışırken okunur/yazılır)

## Çalıştırma (özet)
1. Maven/Gradle ile bağımlılıkları ekleyin (ör. Gson veya Jackson).
2. data/patients.json dosyasını oluşturun (başlangıç için boş bir şablon).
3. Uygulamayı IDE üzerinden veya `mvn package` ardından `java -jar` ile çalıştırın.
4. Doctor veya hasta kullanıcı adı/şifre ile giriş yaparak özellikleri deneyin.

## Geliştirme & Katkı
- Yeni özellik eklemek için:
  - Yeni com.follow_disease.com.follow_disease.controller.model sınıfları ekleyin, JSON serileştirme kurallarını güncelleyin.
  - Service katmanında veri okuma/yazma tutarlılığını sağlayın.
  - UI tarafında "Hastalarım" listesi ve detay formu ekleyin.
- Kodlama standardı: Anlaşılır isimlendirme, null kontrolleri, exception handling.
- İstekler (issues) ve PR'lar açarak katkıda bulunabilirsiniz.

## Gelecek İyileştirmeler (fikirler)
- Kullanıcı yetkilendirme (rol tabanlı erişim).
- JSON yerine veritabanına (SQLite / PostgreSQL) geçiş.
- API sunucu (REST) yapısı ile frontend ayrımı.
- Test otomasyonu (JUnit) ve sürekli entegrasyon.

## Lisans
Proje lisansı eklenmemiştir — uygun bir açık kaynak lisansı (MIT, Apache-2.0 vb.) eklenmesi önerilir.
