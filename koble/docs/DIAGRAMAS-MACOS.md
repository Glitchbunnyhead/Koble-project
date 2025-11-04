# Guia de geração de diagramas (macOS)

Este guia explica como instalar as dependências e renderizar os diagramas do projeto no macOS:
- Diagrama de classes: `docs/diagram-model.puml`
- Diagrama ER: `docs/er/er.puml`
- Diagramas C4: `docs/c4/C1-context.puml`, `docs/c4/C2-containers.puml`, `docs/c4/C3-components-api.puml`
- (Opcional) Exportar a partir de `docs/c4/workspace.dsl` usando Structurizr CLI

---

## 1) Pré-requisitos
- Java Runtime (JRE 17+)
- Graphviz (comando `dot` no PATH)
- PlantUML (recomendado usar versão recente)
- (Opcional) Structurizr CLI

---

## 2) Instalação com Homebrew (recomendado)
1. Instale o Homebrew (se ainda não tiver): https://brew.sh
2. Instale Java, Graphviz e PlantUML:
   ```bash
   # Java Temurin 17 (JRE/JDK)
   brew install --cask temurin17

   # Graphviz e PlantUML
   brew install graphviz plantuml
   ```
3. Reinicie o terminal para garantir que o `java`, `dot` e `plantuml` estejam no PATH.

Verificações rápidas:
```bash
java -version
plantuml -version
which dot
```

---

## 3) Instalação manual (sem Homebrew)
- Java: instale o Temurin (https://adoptium.net/) e garanta `java` no PATH.
- Graphviz: baixe o instalador (https://graphviz.org/download/) e garanta `dot` no PATH.
- PlantUML (JAR):
  ```bash
  V=1.2024.8
  mkdir -p "$HOME/tools/plantuml"
  curl -L -o "$HOME/tools/plantuml/plantuml-$V.jar" \
    "https://github.com/plantuml/plantuml/releases/download/v$V/plantuml-$V.jar"
  ```
  Uso:
  ```bash
  java -jar "$HOME/tools/plantuml/plantuml-$V.jar" -version
  ```
- (Opcional) Structurizr CLI (JAR):
  ```bash
  mkdir -p "$HOME/tools/structurizr"
  # Substitua X.Y.Z pela última versão do release
  curl -L -o "$HOME/tools/structurizr/structurizr-cli.jar" \
    "https://github.com/structurizr/cli/releases/download/vX.Y.Z/structurizr-cli-X.Y.Z.jar"
  ```

---

## 4) Renderizar os diagramas com PlantUML
Na raiz do projeto (pasta `koble`), no Terminal.

- Usando o PlantUML do Homebrew:
  ```bash
  plantuml docs/diagram-model.puml
  plantuml docs/er/er.puml
  plantuml docs/c4/C1-context.puml
  plantuml docs/c4/C2-containers.puml
  plantuml docs/c4/C3-components-api.puml
  ```

- Usando o JAR do PlantUML (manual):
  ```bash
  V=1.2024.8
  java -jar "$HOME/tools/plantuml/plantuml-$V.jar" docs/diagram-model.puml
  java -jar "$HOME/tools/plantuml/plantuml-$V.jar" docs/er/er.puml
  java -jar "$HOME/tools/plantuml/plantuml-$V.jar" docs/c4/C1-context.puml
  java -jar "$HOME/tools/plantuml/plantuml-$V.jar" docs/c4/C2-containers.puml
  java -jar "$HOME/tools/plantuml/plantuml-$V.jar" docs/c4/C3-components-api.puml
  ```

As imagens `.png` serão geradas nas mesmas pastas dos `.puml`.

---

## 5) C4-PlantUML: includes remotos vs locais
Os arquivos C4 usam `!include` da internet por padrão.

- Opção A (rápida, requer internet): habilite includes remotos apenas durante a execução.
  ```bash
  export PLANTUML_SECURITY_PROFILE=UNSECURE
  plantuml docs/c4/C1-context.puml
  ```

- Opção B (recomendado): vendorizar includes localmente para não depender da internet.
  ```bash
  mkdir -p docs/c4/lib
  curl -L -o docs/c4/lib/C4_Context.puml   https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml
  curl -L -o docs/c4/lib/C4_Container.puml https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml
  curl -L -o docs/c4/lib/C4_Component.puml https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
  ```
  Depois, edite os `.puml` em `docs/c4/` substituindo as linhas de include remoto por caminhos locais, por exemplo:
  ```
  !include ./lib/C4_Context.puml
  !include ./lib/C4_Container.puml
  !include ./lib/C4_Component.puml
  ```
  Agora, renderize normalmente sem `PLANTUML_SECURITY_PROFILE`.

---

## 6) Exportar a partir do Structurizr DSL (opcional)
Com o Structurizr CLI (JAR) baixado:
```bash
# Exportar para PlantUML (gera .puml em docs/c4/out)
java -jar "$HOME/tools/structurizr/structurizr-cli.jar" \
  export -workspace docs/c4/workspace.dsl -format plantuml -o docs/c4/out

# Exportar para PNG diretamente
java -jar "$HOME/tools/structurizr/structurizr-cli.jar" \
  export -workspace docs/c4/workspace.dsl -format png -o docs/c4/out

# Renderizar os .puml exportados com PlantUML (se necessário)
plantuml docs/c4/out/*.puml
```

---

## 7) Uso em IDE (opcional)
- VS Code: instale a extensão "PlantUML" para visualizar/exportar `.puml`.
- IntelliJ IDEA: instale o plugin PlantUML Integration. Para diagramas Java, também há o diagrama nativo (clique direito no pacote → Diagrams).

---

## 8) Problemas comuns
- "IllegalArgumentException" ao renderizar C4: versão antiga do PlantUML. Use o PlantUML do Homebrew ou o JAR recente (≥ 1.2023.x).
- "dot" não encontrado: Graphviz não está no PATH. Reabra o terminal ou reinstale o Graphviz.
- Includes remotos bloqueados: use `export PLANTUML_SECURITY_PROFILE=UNSECURE` temporariamente, ou vendorize os includes conforme a seção 5.
