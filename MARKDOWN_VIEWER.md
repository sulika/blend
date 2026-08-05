# Blend — Local Markdown Workspace Viewer

A local Markdown viewer inspired by Grip, focused on navigating multiple local Markdown directories. Built with Spring Boot 3.4.3, flexmark-java, Thymeleaf, and Jsoup.

---

## Configuring sources

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

**Fields:**

| Field | Description |
|---|---|
| `id` | URL slug used in routes — must be unique, no spaces |
| `name` | Human-readable label shown on the home page |
| `root` | Absolute path to the directory on your filesystem |
| `indexFile` | File opened at `/markdown/source/{id}` (defaults to `README.md`) |

---

## Running locally

**Prerequisites:** Java 21, Maven 3.9+

```bash
cd blend
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

**Running tests:**

```bash
./mvnw test
```

---

## URL structure

| URL | Description |
|---|---|
| `GET /markdown` | Home page — lists all configured sources |
| `GET /markdown/source/{id}` | Opens the `indexFile` for that source |
| `GET /markdown/source/{id}/view/{path}` | Renders any `.md` file inside the source root |
| `GET /markdown/source/{id}/raw/{path}` | Serves raw files (images, CSS, etc.) from the source root |

### Example URLs

Given a source with `id: english-teaching` and `root: /Users/matias/docs/english-teaching`:

```
http://localhost:8080/markdown
http://localhost:8080/markdown/source/english-teaching
http://localhost:8080/markdown/source/english-teaching/view/grammar/tenses.md
http://localhost:8080/markdown/source/english-teaching/raw/images/diagram.png
```

---

## Link rewriting

Markdown links to local `.md` files are automatically rewritten to app routes.

**Input Markdown** (current page: `grammar/tenses.md`):
```markdown
[Past Simple](./past-simple.md)
[Home](../README.md)
[Examples](./past-simple.md#examples)
![Diagram](./images/diagram.png)
```

**Rendered HTML links:**
```
/markdown/source/english-teaching/view/grammar/past-simple.md
/markdown/source/english-teaching/view/README.md
/markdown/source/english-teaching/view/grammar/past-simple.md#examples
/markdown/source/english-teaching/raw/grammar/images/diagram.png
```

External links (`https://...`) are left unchanged.

---

## Package structure

```
com.platform
├── BlendApplication.java
└── markdown/
    ├── config/
    │   ├── MarkdownViewerProperties.java   # @ConfigurationProperties binding
    │   ├── MarkdownSourceConfig.java       # per-source settings
    │   └── MarkdownHtmlConfig.java         # HTML/theme settings
    ├── controller/
    │   └── MarkdownController.java         # HTTP endpoints
    ├── service/
    │   ├── MarkdownSourceService.java      # source lookup
    │   ├── MarkdownPathSecurityService.java # path traversal prevention
    │   ├── MarkdownRenderService.java      # flexmark Markdown → HTML
    │   ├── MarkdownLinkRewriteService.java # Jsoup link rewriting
    │   └── MarkdownFileTreeService.java    # sidebar file tree
    └── dto/
        ├── MarkdownPageDto.java
        ├── MarkdownSourceDto.java
        └── MarkdownTreeNodeDto.java
```

---

## Known limitations

- **No authentication.** This tool is for local use only. Do not expose it to the internet.
- **No search.** Full-text search across sources is not implemented.
- **No live reload.** The file tree and content are fetched fresh on every request, but the browser does not watch for filesystem changes.
- **CSS via classpath only.** The `css` config paths starting with `./` are resolved as static web resources (served from `src/main/resources/static/`). Absolute filesystem paths for CSS are not supported in this version.
- **Mermaid / code highlighting.** Syntax highlighting and diagram rendering are not included. You can add them via `injectBodyEnd` with a CDN-free script.
- **Large trees.** The sidebar renders the complete file tree on every page load. Very large directories may slow page rendering.
