# Source recuperada de `app.apk`

Este pacote foi gerado por descompilação autorizada do APK fornecido. Ele contém o máximo de código e recursos recuperáveis, mas não é necessariamente o projeto original: comentários, nomes locais, estrutura Gradle, arquivos de configuração e código removido/ofuscado podem não ser restaurados exatamente.

## Conteúdo

- `jadx_decompiled/`: código Java gerado pelo JADX a partir de `classes.dex`, incluindo classes auxiliares e um esqueleto Gradle exportado.
- `original_java_backups/`: os arquivos `.java.bak` que já estavam embutidos no APK e preservam nomes/estrutura mais próximos da origem.
- `resources/`: imagens, XMLs, layout e `AndroidManifest.xml` convertido para uma forma editável.
- `metadata/SHA256SUM.txt`: hash do APK analisado.

## Observações importantes

O DEX usa renomeação/ofuscação em parte dos pacotes (`com.p001s` no output do JADX), enquanto os arquivos `.java.bak` usam `com.s`. Pode ser necessário alinhar os pacotes antes de compilar. O projeto exportado pelo JADX é uma base de leitura e não uma garantia de build imediato; recursos Android compilados (`resources.arsc`) e IDs de recursos não equivalem automaticamente a um projeto Gradle original.

O APK declara `SYSTEM_ALERT_WINDOW`, inicia `OverlayService` e contém opções de interface relacionadas a jogos. Este pacote apenas recupera o código existente; não adiciona funcionalidades novas nem remove proteções.
