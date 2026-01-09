### Fund Search Application 
A Spring Boot 3 application for managing fund data with Excel import, PostgreSQL storage, and Elasticsearch-powered partial search capabilities.  

### Import fund data from Excel (.xlsx)  
-Persist data in PostgreSQL  
-Asynchronous indexing to Elasticsearch  
-Partial text search using edge n-gram  
-Filtering by fund type   
-Sorting and pagination  
-Reindexing from database to Elasticsearch  

### Technologies  
Java 17  
Spring Boot 3.2.1  
PostgreSQL - 16.2  
Elasticsearch 8.11  
Apache POI 5.2.5  
Docker & Docker Compose

### Docker ile Çalıştırma  

Clone the repository  
git clone https://github.com/furkancan0/fund-search-service.git  
cd fund-search-service 

Start PostgreSQL and Elasticsearch  
docker-compose up -d  

curl http://localhost:9200  

Run Spring Boot application  
mvn clean install  
mvn spring-boot:run  

Application will start on:
http://localhost:8080  

Elasticsearch'e index oluşturmak için Excel dosyasını yükleyin.  
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

Not: Sayfalandırma,  1 tabanlıdır ve Spring'in 0 tabanlı sayfalandırmasıyla eşleştirilir.

HSBC portföy by code  
Fon koduna göre arama  
http://localhost:8080/api/funds/searchByCode?code=HBF  

database tekrar indexleme  
http://localhost:8080/api/funds/reindex  
