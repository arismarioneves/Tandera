# Tandera - Onde Assistir 🎬

<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="Tandera - Onde Assistir" style="width:100px;"/>

##

**Tandera** é um serviço de busca que filtra diferentes serviços de streaming e mostra onde você pode assistir legalmente aos seus filmes e séries preferidos.

> **Inspiração** - Tandera é uma lente que nos aproxima de uma realidade impossível de ver com um olhar humano. É uma visão de longo alcance.

## Projeto

O Tandera começou em 2018 como uma brincadeira e acabou se tornando um projeto sério. Foi desenvolvido com muita pesquisa e em um momento onde praticamente só existia o [JustWatch](https://www.justwatch.com/) com uma premissa semelhante.

## Ciclo de Vida

Todo o versionamento foi feito localmente usando ZIPs (sim, ZIPs!), e com muito Stack Overflow e Google. O projeto foi desenvolvido em Java com a arquitetura MVP e algumas bibliotecas como Moxy, Dagger, Retrofit, ButterKnife e a API de animação do Android. A API foi desenvolvida em PHP e o banco de dados em MySQL.

O projeto foi descontinuado em 2021 e retomado em 2026 na versão 3.0, reescrita do zero (veja abaixo).

Powered by TMDB - _Este produto usa a API do TMDB, mas não é endossado nem certificado pelo [TMDB](https://www.themoviedb.org/)_. Disponibilidade em streaming: [JustWatch](https://www.justwatch.com/).

## Versão 3.0

App Android nativo em **Kotlin + Jetpack Compose (Material 3)**, sem anúncios, sem Firebase/analytics e sem nenhuma coleta de dados.

- **Início:** filmes em alta, nos cinemas, em breve, séries em alta e no ar ("Ver tudo" carrega por página).
- **Buscar:** filmes e séries, com filtro de tipo.
- **Título:** onde assistir no país escolhido (assinatura, grátis, anúncios, aluguel, compra), trailer, sinopse, compartilhar.
- **Assistidos:** lista local (DataStore). A lista da versão 2.x (SQLite `tandera.db`) é importada automaticamente na atualização.
- **Ajustes:** país dos streamings (BR, PT, US, AR, MX), política de privacidade, contato.

O app fala só com a [API Tandera](https://ae8.com.br/tandera/api/) (repositório TanderaWEB). A chave do TMDB fica no servidor, não no APK.

### Estrutura

```
app/src/main/java/com/mari05lim/tandera/
  TanderaApplication.kt   dependências (OkHttp, API, DataStore) e carregador de imagens (Coil)
  MainActivity.kt         splash, edge-to-edge, Compose
  data/                   cliente da API, modelos, assistidos/ajustes, importação da v2
  ui/                     navegação, tema e uma pasta por tela (tela + ViewModel)
```

Dependências: AndroidX (Compose, Navigation, Lifecycle, DataStore, SplashScreen), OkHttp, kotlinx.serialization e Coil. Sem DI framework: o grafo cabe em `TanderaApplication`.

### Build

Requisitos: Android Studio recente (AGP 9.4, Gradle 9.8, JDK 17+), SDK 37. `minSdk` 26.

```
gradlew assembleDebug       # instala como com.mari05lim.tandera.debug, ao lado da versão da loja
gradlew testDebugUnitTest
gradlew bundleRelease       # requer configurar a assinatura de release
```

O app se identifica na API com a chave `tandera.apiKey` (mesmo valor de `APP_API_KEY` no `.env` do TanderaWEB), enviada no cabeçalho `X-Tandera-Key`. Ela fica fora do Git: coloque em `local.properties` (`tandera.apiKey=...`) ou passe `-Ptandera.apiKey=...`. O build de release falha sem ela.

O ícone é vetorial (`res/drawable/ic_launcher_foreground.xml`, adaptativo e monocromático); a arte-mestre e as imagens da Play Store ficam em `tools/imagens`, e o texto da ficha em `tools/texto-playstore.txt`.

A URL da API vem de `tandera.apiUrl` em `gradle.properties`. Para testar contra a API local no celular:

```
php -S 127.0.0.1:8099                    # na pasta do TanderaWEB
adb reverse tcp:8099 tcp:8099
gradlew installDebug -Ptandera.apiUrl=http://127.0.0.1:8099/api/
```

O build de debug libera HTTP só para `127.0.0.1` e `10.0.2.2` (emulador). O release exige HTTPS.

## Versões

Assim era organizado o versionamento do Tandera, cada ZIP teve uma história e um propósito:

01.  **BuscarFilmes.zip**
02.  **BuscarFilmes 26052018.zip**
03.  **OndeAssistir 01062018.zip**
04.  **OndeAssistir 08062018.zip**
05.  **OndeAssistir 09062018.zip**
06.  **OndeAssistir 10062018.zip**
07.  **OndeAssistir 11062018** - Everything OK.zip
08.  **OndeAssistir 12062018** - The Best.zip
09.  **OndeAssistir 13062018** - OK.zip
10.  **OndeAssistir 18062018** - Every Things is Good.zip
11.  **OndeAssistir 19062018** - Good Good.zip
12.  **OndeAssistir 20062018** - Oh yeah.zip
13.  **OndeAssistir 23062018** - I am going to Mon.zip
14.  **OndeAssistir 27062018** - Tomorrow has Brazil.zip
15.  **Tandera 30062018** - First.zip
16.  **Tandera 01072018** - Complete.zip
17.  **Tandera 02072018** - Go Brazil.zip
18.  **Tandera 03072018** - Big Day.zip
19.  **Tandera 04072018** - This is Me.zip
20.  **Tandera 04072018** - Let is go to work.zip
21.  **Tandera 04072018** - Ow Man it Work.zip
22.  **Tandera 05072018** - I have to Go Now.zip
23.  **Tandera 09072018** - Go Man.zip
24.  **Tandera 10072018** - Ain, Gostei.zip
25.  **Tandera 12072018** - Mon is waiting for Me.zip
26.  **Tandera 13072018** - It is Friday.zip
27.  **Tandera 16072018** - God is Alive.zip
28.  **Tandera 17072018** - Today have Cinema with Her.zip
29.  **Tandera 18072018** - Empty I got you.zip
30.  **Tandera 19072018** - Ouch, my head is broken.zip
31.  **Tandera 24072018** - I Encrypt IT.zip
32.  **Tandera 25072018** - Shazamm.zip
33.  **Tandera 26072018** - Final 1.0.zip
34.  **Tandera 30072018** - Lets is begin again.zip
35.  **Tandera 31072018** - TVShows.zip
36.  **Tandera 01082018** - It is going on.zip
37.  **Tandera 02082018** - Semi.zip
38.  **Tandera 03082018** - Final 1.1.zip
39.  **Tandera 06082018** - The class back now.zip
40.  **Tandera 07082018** - Nothing New.zip
41.  **Tandera 08082018** - Final 1.2.zip
42.  **Tandera 09082018** - I hate Butter Knife e Moxy.zip
43.  **Tandera 14082018** - Finaly I will go see Her.zip
44.  **Tandera 16082018** - Project is Updated less APT.zip
45.  **Tandera 17082018** - Now I have Prices.zip
46.  **Tandera 20082018** - Ow I need a Server PHP.zip
47.  **Tandera 22082018** - New Loading.zip
48.  **Tandera 23082018** - MVP is pretty.zip
49.  **Tandera 24082018** - Sextou.zip
50.  **Tandera 28082018** - We have Ads.zip
51.  **Tandera 29082018** - Before run update.zip
52.  **Tandera 30082018** - Final 1.3.zip
53.  **Tandera 07092018** - Stream e Theme.zip
54.  **Tandera 15092018** - Final 1.4.zip
55.  **Tandera 16092018** - Final 1.5.zip
56.  **Tandera 20092018** - I hava test now.zip
57.  **Tandera 21092018** - Yay , Two Language.zip
58.  **Tandera 23092018** - Final 1.6.zip
59.  **Tandera 24092018** - Go go warnings.zip
60.  **Tandera 25092018** - Final 1.7.zip
61.  **Tandera 05102018** - Better.zip
62.  **Tandera 31102018** - Update is coming.zip
63.  **Tandera 07012019** - Before next Update.zip
64.  **Tandera 12012019** - Final 1.8.zip
65.  **Tandera 12072020** - Oh GOD, the foods.zip
66.  **Tandera 28062020** - Sleep because it is monday.zip
67.  **Tandera 22072020** - It is vacation, times to work.zip
68.  **Tandera 22072020** - Aquele do Despertar!.zip
69.  **Tandera 23072020** - Now need shortcuts xml.zip
70.  **Tandera 23072020** - Save me.zip
71.  **Tandera 23072020** - The best of new.zip
72.  **Tandera 24072020** - Time to stop and sleep.zip
73.  **Tandera 25072020** - Man, it is 04h40.zip
74.  **Tandera 25072020** - Final 2.0.zip
75.  **Tandera 10082020** - I have to pay SSL.zip
76.  **Tandera 11082020** - Final 2.1.zip
77.  **Tandera 16112020** - TCC Version.zip
78.  **Tandera 16112020** - Final 2.2.zip
79.  **Tandera 14122020** - Final 2.3.zip

* * *

Espero que o **Tandera** possa ser útil para você tanto quanto foi divertido desenvolvê-lo. Boa diversão e boas maratonas de séries e filmes! 🍿🎥
