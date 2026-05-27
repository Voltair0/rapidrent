# RapidRent Frontend Thymeleaf

Am adăugat o interfață web accesibilă din browser, construită cu Thymeleaf, CSS și JavaScript.

## Rute principale

- `/` redirecționează către `/ui`
- `/ui` - pagina principală cu formular de căutare
- `/ui/cars` - listă mașini disponibile + rezervare
- `/ui/login` - autentificare + resetare parolă
- `/ui/register` - înregistrare client/furnizor
- `/ui/client` - dashboard client și istoric rezervări
- `/ui/verification` - încărcare CI și permis
- `/ui/provider` - dashboard furnizor
- `/ui/provider/cars/new` - adăugare mașină
- `/ui/admin` - dashboard admin, moderare mașini și documente

## Conturi demo

Dacă baza de date este goală, aplicația creează automat:

- Admin: `admin@rapidrent.ro` / `Admin123!`
- Client: `client@rapidrent.ro` / `Client123!`
- Furnizor: `furnizor@rapidrent.ro` / `Furnizor123!`

Poți opri datele demo din `application.properties`:

```properties
rapidrent.demo-data.enabled=false
```

## Modificări backend făcute pentru funcționalitate completă

- Am adăugat `spring-boot-starter-thymeleaf` în `pom.xml`.
- Am permis rutele `/`, `/ui/**`, `/css/**`, `/js/**` în `SecurityConfig`.
- Am adăugat endpoint `/api/auth/me`, folosit de frontend ca să știe ID-ul, rolul și statusul documentelor utilizatorului autentificat.
- Am adăugat listare rezervări client, listare flotă furnizor și listare documente pending pentru admin.
- Am extins entitatea `Car` cu câmpuri opționale pentru afișare și filtre: `location`, `category`, `transmission`, `seats`, `imageUrl`.
- Am ascuns `provider` din serializarea JSON a mașinii, pentru a evita expunerea parolelor și problemele cu lazy loading.

## Observație

Frontend-ul folosește JWT în `localStorage` și trimite automat headerul `Authorization: Bearer <token>` pentru rutele protejate.
