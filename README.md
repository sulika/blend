# Blend — Local Markdown Workspace Viewer

Blend is a local Markdown viewer inspired by [Grip](https://github.com/joeyespo/grip), built to browse and navigate multiple local Markdown directories from your browser. It's a Java / Spring Boot project, built with Spring Boot 3.4.3, flexmark-java, Thymeleaf, and Jsoup.

This project is open source — see [License](#license) and [Contributing](#contributing) below.

---

## Motivation

I use Markdown a lot — mostly because I use a lot of AI/LLMs, and Markdown is the format they produce and reason about most naturally. Reading that output back as plain text gets tedious fast, so it's much more convenient to have a proper reader: rendered headings, tables, code blocks, and clickable links between notes, instead of raw `.md` files in an editor.

Blend scratches that itch: point it at one or more local folders full of Markdown notes and browse them like a lightweight wiki, entirely on your own machine.

---

## Usage

### Prerequisites

- Java 21
- Maven 3.9+ (or use the bundled `./mvnw` wrapper)

### Configure your sources

Add your Markdown directories to `src/main/resources/application.yml`:

```yaml
app:
  markdown-viewer:
    enabled: true
    html:
      title: "My Notes"
      theme: github
      css:
        - ./themes/github.css        # bundled — resolves to /themes/github.css
        - /themes/custom.css         # your own file under src/main/resources/static/
      # injectHead: ./templates/head.html       # optional HTML injected into <head>
      # injectBodyEnd: ./templates/body-end.html # optional HTML before </body>
    sources:
      - id: english-teaching
        name: English Teaching
        root: /Users/matias/docs/english-teaching
        indexFile: README.md
      - id: architecture-notes
        name: Architecture Notes
        root: /Users/matias/docs/architecture
        indexFile: index.md
```

| Field | Description |
|---|---|
| `id` | URL slug used in routes — must be unique, no spaces |
| `name` | Human-readable label shown on the home page |
| `root` | Absolute path to the directory on your filesystem |
| `indexFile` | File opened at `/markdown/source/{id}` (defaults to `README.md`) |

### Run it

```bash
./mvnw spring-boot:run
# or
make run
```

The app starts on `http://localhost:8080` by default.

Other handy commands (see the `Makefile`):

```bash
make build    # compile without running tests
make test     # run unit tests
make package  # build an executable JAR
make clean    # remove build artifacts
```

### Browse

| URL | Description |
|---|---|
| `GET /markdown` | Home page — lists all configured sources |
| `GET /markdown/source/{id}` | Opens the `indexFile` for that source |
| `GET /markdown/source/{id}/view/{path}` | Renders any `.md` file inside the source root |
| `GET /markdown/source/{id}/raw/{path}` | Serves raw files (images, CSS, etc.) from the source root |

Given a source with `id: english-teaching` and `root: /Users/matias/docs/english-teaching`:

```
http://localhost:8080/markdown
http://localhost:8080/markdown/source/english-teaching
http://localhost:8080/markdown/source/english-teaching/view/grammar/tenses.md
http://localhost:8080/markdown/source/english-teaching/raw/images/diagram.png
```

Local links between `.md` files are automatically rewritten to these app routes, so your notes stay fully navigable — see [MARKDOWN_VIEWER.md](MARKDOWN_VIEWER.md) for the link-rewriting rules, the package layout, and known limitations (no auth, no search, no live reload — this is meant for local use).

---

## Acknowledgments

Blend is built on top of [Spring Boot](https://spring.io/projects/spring-boot), [flexmark-java](https://github.com/vsch/flexmark-java), [Thymeleaf](https://www.thymeleaf.org/), and [Jsoup](https://jsoup.org/), all pulled in as Maven dependencies under their own licenses.

Syntax highlighting is powered by a vendored copy of [highlight.js](https://github.com/highlightjs/highlight.js) v11.9.0 (`src/main/resources/static/hljs/`), licensed under BSD-3-Clause, © the highlight.js authors and contributors.

---

## Contributing

This is a personal project, but proposals and pull requests are welcome. If you'd like to suggest a change:

1. Open an issue describing the problem or idea, or
2. Fork the repo and open a pull request directly.

Since Blend is a Java/Spring Boot project, please keep contributions consistent with the existing package structure under `com.platform.markdown` and include tests for new service-level logic where practical (`./mvnw test`).

---

## License

Blend is released under the [MIT License](LICENSE).
