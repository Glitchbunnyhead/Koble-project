# Guia de geração de diagramas (Windows)

Este guia explica como instalar as dependências e renderizar os diagramas do projeto no Windows:
- Diagrama de classes: `docs/diagram-model.puml`
- Diagrama ER: `docs/er/er.puml`
- Diagramas C4: `docs/c4/C1-context.puml`, `docs/c4/C2-containers.puml`, `docs/c4/C3-components-api.puml`
- (Opcional) Exportar a partir de `docs/c4/workspace.dsl` usando Structurizr CLI

---

## 1) Pré-requisitos
- Java Runtime (JRE 17+)
- Graphviz (comando `dot` no PATH)
- PlantUML (recomendado usar o JAR mais recente)
- (Opcional) Structurizr CLI

---

## 2) Instalação com Chocolatey (recomendado)
1. Abra PowerShell como Administrador e instale o Chocolatey:
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force;
   [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072;
   iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   ```
2. Instale Java, Graphviz e PlantUML:
   ```powershell
   choco install -y temurin17-jre graphviz plantuml
   ```
3. (Opcional) Instale Structurizr CLI:
   ```powershell
   choco install -y structurizr-cli
   ```
4. Feche e reabra o PowerShell para atualizar o PATH.

---

## 3) Instalação manual (sem Chocolatey)
- Java: instale o Temurin JRE (https://adoptium.net/)
- Graphviz: baixe o instalador (https://graphviz.org/download/) e finalize com `dot` no PATH
- PlantUML (JAR):
  ```powershell
  $V = '1.2024.8'
  New-Item -ItemType Directory -Force $env:USERPROFILE\tools\plantuml | Out-Null
  Invoke-WebRequest "https://github.com/plantuml/plantuml/releases/download/v$V/plantuml-$V.jar" -OutFile "$env:USERPROFILE\tools\plantuml\plantuml-$V.jar"
  ```
- (Opcional) Structurizr CLI: baixe de https://github.com/structurizr/cli/releases e adicione ao PATH (ou use o `.jar`).

---

## 4) Renderizar os diagramas com PlantUML
Na raiz do projeto (pasta `koble`), abra PowerShell.

- Usando o PlantUML do Chocolatey:
  ```powershell
  plantuml docs\diagram-model.puml
  plantuml docs\er\er.puml
  plantuml docs\c4\C1-context.puml
  plantuml docs\c4\C2-containers.puml
  plantuml docs\c4\C3-components-api.puml
  ```

- Usando o JAR do PlantUML (manual):
  ```powershell
  $V = '1.2024.8'
  java -jar "$env:USERPROFILE\tools\plantuml\plantuml-$V.jar" docs\diagram-model.puml
  java -jar "$env:USERPROFILE\tools\plantuml\plantuml-$V.jar" docs\er\er.puml
  java -jar "$env:USERPROFILE\tools\plantuml\plantuml-$V.jar" docs\c4\C1-context.puml
  java -jar "$env:USERPROFILE\tools\plantuml\plantuml-$V.jar" docs\c4\C2-containers.puml
  java -jar "$env:USERPROFILE\tools\plantuml\plantuml-$V.jar" docs\c4\C3-components-api.puml
  ```

As imagens `.png` serão geradas nas mesmas pastas dos `.puml`.

---

## 5) C4-PlantUML: includes remotos vs locais
Os arquivos C4 usam `!include` da internet por padrão.

- Opção A (rápida, requer internet): habilite includes remotos apenas durante a execução.
  ```powershell
  $env:PLANTUML_SECURITY_PROFILE = 'UNSECURE'
  plantuml docs\c4\C1-context.puml
  ```

- Opção B (recomendado): vendorizar includes localmente para não depender da internet.
  ```powershell
  New-Item -ItemType Directory -Force docs\c4\lib | Out-Null
  Invoke-WebRequest https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml   -OutFile docs\c4\lib\C4_Context.puml
  Invoke-WebRequest https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml -OutFile docs\c4\lib\C4_Container.puml
  Invoke-WebRequest https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml -OutFile docs\c4\lib\C4_Component.puml
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
Com o Structurizr CLI instalado, você pode exportar as views definidas em `docs/c4/workspace.dsl` para PlantUML/PNG:
```powershell
# Exportar para PlantUML (gera .puml em docs\c4\out)
structurizr export -workspace docs\c4\workspace.dsl -format plantuml -o docs\c4\out

# Exportar para PNG diretamente
structurizr export -workspace docs\c4\workspace.dsl -format png -o docs\c4\out

# Renderizar os .puml exportados com PlantUML (se necessário)
plantuml docs\c4\out\*.puml
```

---

## 7) Uso em IDE (opcional)
- VS Code: instale a extensão "PlantUML" para visualizar/exportar `.puml`.
- IntelliJ IDEA: instale o plugin PlantUML Integration. Para diagramas Java, também há o diagrama nativo (clique direito no pacote → Diagrams).

---

## 8) Problemas comuns
- "IllegalArgumentException" ao renderizar C4: versão antiga do PlantUML. Use o JAR recente (≥ 1.2023.x) ou o pacote do Chocolatey.
- "dot" não encontrado: Graphviz não está no PATH. Reabra o terminal ou reinstale o Graphviz.
- Includes remotos bloqueados: use `$env:PLANTUML_SECURITY_PROFILE = 'UNSECURE'` temporariamente, ou vendorize os includes conforme a seção 5.
