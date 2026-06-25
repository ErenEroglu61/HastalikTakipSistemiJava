# Hastalık Takip Sistemi (Java)

## Katkıda Bulunanlar
- Eren EROĞLU — 170424035  
- Emre OKÇELEN — 170421929  
- Fatma TANRIVERDİ — 170424023  
- Engin ÇETİNTAŞ — 170422026  

Bu proje, doktorların hastalarını, hastaların ise kendi tedavi ve hastalık süreçlerini dijital bir ortamda güvenle takip edebilmeleri amacıyla geliştirilmiş kapsamlı bir otomasyon sistemidir. Temel hedef, Java programlama dili, Nesne Yönelimli Programlama (OOP) prensipleri ve JavaFX ile Grafiksel Kullanıcı Arayüzü (GUI) tasarım tekniklerini gerçek hayat senaryolarına uygun bir uygulamada birleştirmektir.

##  Kullanılan Teknolojiler

* **Programlama Dili:** Java 21
* **Kullanıcı Arayüzü (GUI):** JavaFX
* **Veri Yönetimi:** Gson (Google) kütüphanesi ile hafif ve okunabilir JSON formatında veri saklama
* **Proje Yönetimi:** Maven

##  Temel Özellikler

###  Güvenli Kimlik Doğrulama ve Kayıt Sistemi
* Sisteme kayıt olmak isteyen kullanıcıların TC kimlik numaraları, hastane arşivindeki (hospital_records.json) kayıtlarla çapraz kontrolden geçirilir.
* TC numarası arşivde bulunmayan yetkisiz kişilerin sisteme kaydolması engellenir. 
* E-posta, telefon ve yaş gibi bilgilerin format geçerlilikleri çok katmanlı bir şekilde doğrulanarak sisteme eklenir.

###  Doktor Paneli
* Doktorlar, sadece kendilerine bağlı olan hastaların listesini dinamik bir şekilde görüntüleyebilir.
* Seçilen hastanın güncel sağlık verileri, geçmişe dönük ateş, nabız, tansiyon ve şeker (vital bulgular) ölçümleri detaylı tablolar halinde incelenebilir.
* Hastanın beyan ettiği semptomlar analiz edilip, hastalık seyrine göre reçete kodu, yeni ilaçlar ve tıbbi tavsiyeler sisteme girilebilir.

###  Hasta Paneli
* Hastalar giriş yaptıklarında; aktif hastalıklarını, geçmiş hastalıklarını, şu an kullandıkları ve geçmişte kullandıkları ilaçları dört bölmeli bir panel üzerinden takip edebilir.
* Kullanılan ilaçlara dair kategori bazlı temel yan etkiler ve doktorun o ilaca özel eklediği yan etki uyarıları şeffaf bir şekilde görüntülenir.
* Hastalar, anlık yaşamsal bulgularını (vital bulgular) girip sistemdeki semptomları seçerek tedavi süreçleri hakkında doktorlarına not bırakabilir.

###  Çift Yönlü İletişim ve Bildirimler
* `Feedback` arayüzü sayesinde doktor ve hasta arasında kesintisiz bir bilgi alışverişi (geri bildirim, şikayet, yan etki notları) sağlanır.
* `Notification` arayüzü sayesinde, bir taraf güncelleme yaptığında diğer tarafın hesabına anlık bildirim düşer (Örn: "Hastanızdan gelen güncellemeleriniz var") ve bu durum arayüzde bildirim ikonu (badge) ile görselleştirilir.

##  Yazılım Mimarisi ve OOP Prensipleri

Proje, katmanlı bir yapıda (Database, Service, Controller, Model) tasarlanarak veri katmanı ile arayüz katmanının birbirinden ayrılması hedeflenmiştir:
* **Kalıtım (Inheritance):** Sistemdeki tüm kullanıcılar ortak bir `User` soyut sınıfından, tüm ilaçlar ise `Medicine` soyut sınıfından türetilmiştir.
* **Çok Biçimlilik (Polymorphism):** İlaçlar kendi türlerine (Örn: Antibiyotik, Ağrı Kesici) göre alt sınıflara ayrılarak, ilaca özgü yan etkilerin gösteriminde çok biçimlilik kullanılmıştır.
* **Arayüz (Interface) Kullanımı:** Ortak davranış gerektiren bildirim gönderme (`Notification`) ve geri bildirim (`Feedback`) mekanizmaları sistem genelinde uygulanmıştır.

