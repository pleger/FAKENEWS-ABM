# FAKENEWS-ABM

FAKENEWS-ABM is a fork of `pragmaticslaboratory/SBABM` adapted from cross-border B2C e-commerce to the dissemination of fake news on Social Network Sites (SNSs) such as X or Instagram.

The original model represents buyers, e-commerce marketplaces, word of mouth, and Endorsement theory. This fork keeps the agent-based simulation core but changes the interpretation:

- buyers become SNS users;
- marketplaces become news-source types, such as traditional media, unknown online media, fake-news sources, and mixed sources;
- purchases become repost decisions;
- market share becomes repost share;
- word of mouth becomes contact-based social sharing;
- endorsement attributes come from `datos para simulacion.xlsx`.

The fork is designed for the PLURALISMO project line on evaluating strategies to disseminate fake news using artificial intelligence. In that context, the ABM provides a computational laboratory for comparing how source credibility, content framing, source reach, user contacts, and scenario interventions affect fake-news diffusion.

## Model

Each simulation has a population of SNS users. At every period, each user evaluates the sources they know through accumulated endorsement values. A selected source represents the source whose news the user reposts in that period.

The source attributes are two-level probability distributions (`Bajo`, `Alto`) for variables such as:

- positive and negative evoked emotions;
- simple language;
- political, geographic, and social proximity;
- sensationalism;
- information quality;
- information entertainment value;
- credibility of the source;
- audiovisual content, hashtags, and links.

User attributes are the corresponding mean weights on a 1-7 scale. The optional `WORD OF MOUTH` weight controls how strongly users incorporate recommendations from contacts. The inherited endorsement formula supports negative weights too, which can be useful when an experiment should model aversion to a high level of an attribute, such as sensationalism.

## Inputs

The simulator reads Excel workbooks from `input/` by default. The loader also accepts direct workbook paths and checks `inputs/` for compatibility with the plural folder name.

Generated fake-news inputs:

- `FAKENEWS_BASELINE.xlsx`: plural media ecosystem with WOM enabled.
- `FAKENEWS_NO_WOM.xlsx`: same source ecology with social sharing disabled.
- `FAKENEWS_COORDINATED_PUSH.xlsx`: after period 15, unknown media adopts fake-news-source values for simple language, sensationalism, and entertainment.
- `FAKENEWS_MEDIA_LITERACY.xlsx`: countermeasure-style input with higher quality weighting and lower sensitivity to sensationalism/negative emotion.

Workbook sheets:

- `Configuration`: simulation controls such as `PERIODS`, `AGENTS`, `REPETITIONS`, `WOM`, and report flags.
- `Markets`: source-type endorsement distributions. The original sheet name is preserved for code compatibility.
- `Buyers`: SNS-user endorsement weights. The original sheet name is preserved for code compatibility.
- `MarketQuota`: source reach or visibility probability.
- `Scenario`: optional custom intervention. Use `SCENARIO=-2` and one row with `FROM`, `TO`, `START_PERIOD`, then attribute names to copy.

## CLI

Build:

```sh
make build
```

List available inputs:

```sh
java -cp "build/classes:lib/*" Main --list-inputs
```

Run a scenario:

```sh
java -cp "build/classes:lib/*" Main --input FAKENEWS_BASELINE --no-gui
```

Useful overrides:

```sh
java -cp "build/classes:lib/*" Main \
  --input FAKENEWS_COORDINATED_PUSH \
  --periods 60 \
  --agents 500 \
  --repetitions 20 \
  --no-gui
```

## Tests

Run:

```sh
make test
```

The test suite currently checks:

- endorsement scoring for binary high/low levels;
- loading the generated fake-news workbook;
- applying a custom scenario that copies selected attributes from one source type to another;
- reset behavior for source/user factories across repeated loads.

## Outputs

Each run writes a timestamped folder under `output/`. The output workbook includes the original configuration and input sheets, plus:

- `RepostsPerSource`;
- `UniqueRepostersPerSource`;
- `Results`;
- `DetailedResult`;
- `Endorsements`;
- `ScenarioChanges`.

The Java class names still include some inherited e-commerce terms (`Buyer`, `Market`, `SalesPerMarketData`) to keep the fork small and traceable. User-facing CLI, README, generated inputs, and report sheets use the SNS/fake-news vocabulary.

## License

MIT, inherited from SBABM.
