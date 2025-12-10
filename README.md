# GPS Weather App

Aplikacja Android do śledzenia lokalizacji GPS z integracją danych pogodowych w czasie rzeczywistym.

## 📱 Funkcje

- **Śledzenie GPS** - Wyświetlanie aktualnej lokalizacji na mapie OpenStreetMap
- **Dane pogodowe** - Pobieranie i wyświetlanie informacji o pogodzie dla bieżącej lokalizacji
- **Udostępnianie** - Wysyłanie współrzędnych przez SMS lub inne aplikacje
- **Zrzuty mapy** - Zapisywanie widoku mapy jako obraz PNG
- **Interaktywna mapa** - Zoom, przesuwanie, marker lokalizacji

## 🛠️ Technologie

- **Java** - Język programowania
- **Android SDK** - Framework aplikacji mobilnych
- **OpenStreetMap (osmdroid)** - Biblioteka map
- **OpenWeatherMap API** - Dane pogodowe
- **LocationManager** - Usługi lokalizacji GPS/Network

## 📋 Wymagania

- Android Studio Arctic Fox lub nowszy
- Android SDK 21+ (Android 5.0 Lollipop)
- Urządzenie z GPS lub emulator z włączoną lokalizacją
- Klucz API OpenWeatherMap (darmowy)

## 🚀 Instalacja

### 1. Sklonuj repozytorium
```bash
git clone https://github.com/your-username/gps-weather-app.git
cd gps-weather-app
```

### 2. Konfiguracja API OpenWeatherMap

1. Zarejestruj się na [OpenWeatherMap](https://openweathermap.org/)
2. Utwórz darmowy klucz API
3. W pliku `WeatherActivity.java` zamień:
```java
private static final String API_KEY = "TWOJ_KLUCZ_API";
```

### 3. Uprawnienia w AndroidManifest.xml

Upewnij się, że masz następujące uprawnienia:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### 4. Dodaj aktywności w AndroidManifest.xml
```xml
<activity android:name=".MainActivity" 
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity android:name=".WeatherActivity" />
```

### 5. Struktura plików

Upewnij się, że masz następujące pliki:
```
app/src/main/
├── java/com/example/gpsapp/
│   ├── MainActivity.java
│   └── WeatherActivity.java
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   └── weather.xml
│   ├── drawable/
│   │   └── gradient_background.xml
│   └── menu/
│       └── top_menu.xml
└── AndroidManifest.xml
```

### 6. Utwórz gradient_background.xml

W folderze `res/drawable/` utwórz plik `gradient_background.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:angle="90"
        android:startColor="#87CEEB"
        android:endColor="#E0F6FF"
        android:type="linear" />
</shape>
```

### 7. Zbuduj i uruchom
```bash
./gradlew build
./gradlew installDebug
```

## 📱 Użytkowanie

### Ekran główny (MainActivity)

1. **Mapa** - Wyświetla Twoją aktualną lokalizację z markerem
2. **Dane GPS** - Pokazuje szerokość i długość geograficzną
3. **Pociągnij w dół** - Odśwież status połączenia internetowego

### Menu górne

- **📧 SMS** - Wyślij współrzędne przez SMS
- **💾 Zapisz** - Zapisz zrzut mapy jako PNG
- **📤 Udostępnij** - Udostępnij lokalizację przez inne aplikacje
- **🌤️ Pogoda** - Otwórz ekran pogody

### Ekran pogody (WeatherActivity)

Wyświetla:
- Aktualna temperatura
- Min/Max temperatura
- Warunki pogodowe (np. czyste niebo)
- Wschód/zachód słońca
- Prędkość wiatru
- Ciśnienie atmosferyczne
- Wilgotność

**Przycisk powrotu** - Kliknij X w lewym górnym rogu, aby wrócić do mapy

## 🔧 Zależności

W pliku `build.gradle (Module: app)` dodaj:
```gradle
dependencies {
    implementation 'org.osmdroid:osmdroid-android:6.1.14'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
    implementation 'com.google.android.material:material:1.9.0'
}
```

## 🐛 Rozwiązywanie problemów

### Brak lokalizacji GPS

- Upewnij się, że GPS jest włączony w urządzeniu
- Sprawdź, czy aplikacja ma uprawnienia do lokalizacji
- Wypróbuj na zewnątrz lub przy oknie dla lepszego sygnału

### Błąd API pogody

- Sprawdź, czy klucz API jest poprawny
- Upewnij się, że masz połączenie z internetem
- Darmowy plan OpenWeatherMap ma limit 60 zapytań/minutę

### Mapa się nie ładuje

- Sprawdź połączenie internetowe
- Upewnij się, że uprawnienie INTERNET jest dodane
- Wyczyść cache aplikacji

## 📄 Licencja

Ten projekt jest licencjonowany na warunkach licencji MIT.

## 👨‍💻 Autor

Projekt stworzony jako aplikacja edukacyjna do nauki integracji GPS i API pogodowych w Androidzie.

## 🤝 Wkład

Pull requesty są mile widziane. W przypadku większych zmian, najpierw otwórz issue, aby omówić, co chcesz zmienić.

## 📞 Kontakt

Jeśli masz pytania lub sugestie, otwórz issue na GitHubie.

---

**Uwaga**: Pamiętaj, aby nie udostępniać swojego klucza API w publicznych repozytoriach. Użyj pliku konfiguracyjnego lub zmiennych środowiskowych.