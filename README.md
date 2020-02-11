# Code assignment for WAES
By: Cristhian Cangrejo S

Hello guys from WAES, I have implemented the solution for the assignment based on the next frameworks:

- Spring Boot
- Redis
- Java 8
- Swagger

For testing I have used:

- Mockito
- JUnit

It is based on several controllers that you can find in [Swagger](http://localhost:8080/swagger-ui/index.html?url=/v3/api-docs) `http://localhost:8080/swagger-ui/index.html?url=/v3/api-docs` if deploy:
- GET: /v1/diff/{id}
- POST: /v1/diff/{id}/left _(BASE 64 value)_
- POST: /v1/diff/{id}/right `(BASE 64 value)`

Then you could test the different APIs

**The assignment:**

Provide 2 http endpoints that accepts JSON base64 encoded binary data on both
endpoints

o <host>/v1/diff/<ID>/left and <host>/v1/diff/<ID>/right

    The provided data needs to be diff-ed and the results shall be available on a third end
    point
    
o <host>/v1/diff/<ID>

    The results shall provide the following info in JSON format
    
o If equal return that

o If not of equal size just return that

o If of same size provide insight in where the diffs are, actual diffs are not needed.
    
    So mainly offsets + length in the data