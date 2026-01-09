Fund Search Application  
A Spring Boot 3 application for managing fund data with Excel import, PostgreSQL storage, and Elasticsearch.  

Technologies  
Java 17  
Spring Boot 3.2.1  
PostgreSQL - 16.2  
Elasticsearch 8.11  
Apache POI 5.2.5  

Docker ile Çalıştırma  

git clone https://github.com/furkancan0/fund-search-service.git  
cd fund-search-service  

docker-compose up -d  

curl http://localhost:9200  

mvn clean install  
mvn spring-boot:run  

Aynı dizindeki excel dosyası import  
curl -X POST -F "file=@TakasbankTEFASFon.xlsx" http://localhost:8080/api/funds/upload  

Fon adına göre arama 1 yıl getiri(%) ile büyükten küçüğe sıralanmış biçimde.  
(Pusula portföy)  
http://localhost:8080/api/funds/searchByName?sortBy=oneYear&sortDir=DESC  

{
    "name": "pusul",
    "fundType": "Serbest Şemsiye Fonu",
    "oneYear": {"min": 20, "max": 100}
}

Fon adına göre arama 3 yıl getiri(%) ile küçükten büyüğe sayfalanmış biçimde.  
(Kuveyt türk portföy)  
http://localhost:8080/api/funds/searchByName?sortBy=threeYears&page=1&size=10  

{
    "name": "kuve"
}

HSBC portföy by code  
Fon koduna göre arama  
http://localhost:8080/api/funds/searchByCode?code=HBF  

database tekrar indexleme  
http://localhost:8080/api/funds/reindex  
