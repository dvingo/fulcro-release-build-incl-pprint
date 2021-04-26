Repo demonstrating how to cut out guardrails dev-time dependencies from fulcro release builds.

mainly: cljs.pprint and expound.

https://github.com/fulcrologic/guardrails/blob/develop/src/main/com/fulcrologic/guardrails/noop.cljc

Before:

```
:main [JS: 692.69 KB] [GZIP: 184.34 KB]
```

After:

```
:main [JS: 526.59 KB] [GZIP: 143.4 KB]
```

Development:
```bash
yarn
yarn run dev
# open shadow browser, start a watch for main
```

To run a report:

```bash
yarn run report
```

edit src/main/my_app.cljs and uncomment the fulcro lines, run the report again.

edit: shadow-cljs.edn and uncomment the ns-alias form.

run the report again.
--------------------------------------------------------------------------------

This code produces the following build report:

```clojure
(ns my-app
  (:require
    ["react" :as react]
    ["react-dom" :as react-dom]))

(defn ^:export init []
  (react-dom/render
    (react/createElement "div" #js{} "Hello world")
    (js/document.getElementById "app")))
```

```
| Resource                             |   Optimized |  Total % |          JS |      Source |
|--------------------------------------+-------------+----------+-------------+-------------|
| npm | react-dom                      |    120.2 KB |  90.47 % |    120.5 KB |    122.2 KB |
| npm | react                          |      6.2 KB |   4.65 % |      6.2 KB |      6.8 KB |
| npm | scheduler                      |      4.6 KB |   3.46 % |      4.6 KB |      5.2 KB |
| npm | object-assign                  |    993.0 B  |   0.75 % |    994.0 B  |      2.2 KB |
| jar | shadow/js.js                   |    480.0 B  |   0.36 % |      3.2 KB |      3.2 KB |
| jar | goog/base.js                   |    208.0 B  |   0.16 % |    127.7 KB |    127.7 KB |
|     | my_app.cljs                    |    117.0 B  |   0.09 % |    269.0 B  |    565.0 B  |
|     | shadow/module/main/append.js   |     88.0 B  |   0.07 % |    110.0 B  |    110.0 B  |
```


This code produces the following build report, which includes cljs.pprint amongst
others (expound, spec.gen, etc ):

```clojure
(ns my-app
  (:require
    ["react" :as react]
    ["react-dom" :as react-dom]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as c :refer [defsc]]))

(defsc Hello [this props]
 {:query         []
  :initial-state (fn [_])}
 (react/createElement "div" #js{} "Hello worlds"))

(defonce app (app/fulcro-app {:render-root! react-dom/render}))

(defn ^:export init []
  (app/mount! app Hello "app"))
```

Build report with fulcro:

```
| Resource                                                                               |   Optimized |  Total % |          JS |      Source |
|----------------------------------------------------------------------------------------+-------------+----------+-------------+-------------|
| jar | cljs/core.cljs                                                                   |    192.9 KB |  27.58 % |      1.3 MB |    338.8 KB |
| npm | react-dom                                                                        |    120.2 KB |  17.19 % |    120.5 KB |    122.2 KB |
| jar | cljs/pprint.cljs                                                                 |     90.9 KB |  12.99 % |    510.1 KB |    128.1 KB |
| jar | cljs/spec/alpha.cljs                                                             |     45.8 KB |   6.55 % |    264.3 KB |     54.1 KB |
| jar | expound/alpha.cljc                                                               |     23.0 KB |   3.29 % |    186.9 KB |     40.9 KB |
| jar | edn_query_language/core.cljc                                                     |     22.1 KB |   3.16 % |    169.0 KB |     16.7 KB |
| jar | com/fulcrologic/fulcro/components.cljc                                           |     18.7 KB |   2.68 % |    174.7 KB |     93.9 KB |
| jar | cljs/spec/gen/alpha.cljs                                                         |     18.0 KB |   2.57 % |    101.5 KB |      6.1 KB |
| jar | com/fulcrologic/fulcro/specs.cljc                                                |     17.5 KB |   2.50 % |    142.0 KB |     11.4 KB |
| jar | com/fulcrologic/fulcro/algorithms/tx_processing.cljc                             |     17.2 KB |   2.47 % |    195.0 KB |     46.0 KB |
| jar | taoensso/encore.cljs                                                             |     11.4 KB |   1.62 % |    303.9 KB |    139.5 KB |
| jar | expound/printer.cljc                                                             |     10.6 KB |   1.52 % |     72.8 KB |     14.5 KB |
| jar | com/fulcrologic/fulcro/application.cljc                                          |      6.8 KB |   0.97 % |     74.8 KB |     30.1 KB |
| jar | com/fulcrologic/fulcro/algorithms/merge.cljc                                     |      6.4 KB |   0.92 % |     57.9 KB |     23.4 KB |
| npm | react                                                                            |      6.2 KB |   0.88 % |      6.2 KB |      6.8 KB |
| jar | com/fulcrologic/fulcro/inspect/inspect_client.cljc                               |      5.1 KB |   0.74 % |     60.3 KB |     19.6 KB |
| jar | taoensso/timbre.cljs                                                             |      4.8 KB |   0.69 % |     33.1 KB |     29.5 KB |
| jar | goog/uri/uri.js                                                                  |      4.7 KB |   0.67 % |     44.1 KB |     44.1 KB |
| npm | scheduler                                                                        |      4.6 KB |   0.66 % |      4.6 KB |      5.2 KB |
| jar | com/fulcrologic/fulcro/mutations.cljc                                            |      3.8 KB |   0.54 % |     55.2 KB |     23.1 KB |
| jar | com/fulcrologic/fulcro/data_fetch.cljc                                           |      3.8 KB |   0.54 % |     45.8 KB |     18.6 KB |
| jar | goog/math/long.js                                                                |      3.4 KB |   0.48 % |     27.9 KB |     27.9 KB |
| jar | cljs/reader.cljs                                                                 |      3.1 KB |   0.44 % |     19.4 KB |      7.2 KB |
| jar | com/fulcrologic/fulcro/algorithms/indexing.cljc                                  |      3.0 KB |   0.43 % |     20.6 KB |      6.5 KB |
| jar | expound/ansi.cljc                                                                |      3.0 KB |   0.43 % |     10.5 KB |      2.3 KB |
| jar | com/fulcrologic/fulcro/algorithms/denormalize.cljc                               |      2.8 KB |   0.40 % |     22.5 KB |     11.2 KB |
| jar | com/fulcrologic/fulcro/algorithms/data_targeting.cljc                            |      2.7 KB |   0.39 % |     20.1 KB |      8.1 KB |
| jar | cljs/core/async/impl/channels.cljs                                               |      2.7 KB |   0.39 % |     26.0 KB |      7.9 KB |
| jar | goog/debug/tracer.js                                                             |      2.3 KB |   0.33 % |     25.8 KB |     25.8 KB |
| jar | goog/base.js                                                                     |      2.2 KB |   0.32 % |    127.7 KB |    127.7 KB |
| jar | expound/problems.cljc                                                            |      2.1 KB |   0.30 % |     14.9 KB |      6.3 KB |
| jar | com/fulcrologic/fulcro/algorithms/normalize.cljc                                 |      2.0 KB |   0.29 % |     11.2 KB |      4.8 KB |
| jar | goog/structs/map.js                                                              |      1.8 KB |   0.26 % |     12.5 KB |     12.5 KB |
| jar | clojure/string.cljs                                                              |      1.8 KB |   0.26 % |     14.6 KB |      8.3 KB |
| jar | clojure/set.cljs                                                                 |      1.8 KB |   0.26 % |     14.4 KB |      5.0 KB |
| jar | com/fulcrologic/fulcro/rendering/multiple_roots_renderer.cljc                    |      1.7 KB |   0.24 % |     32.5 KB |     11.6 KB |
| jar | cljs/tools/reader/impl/inspect.cljs                                              |      1.6 KB |   0.23 % |      8.2 KB |      2.7 KB |
| jar | cljs/core/async/impl/buffers.cljs                                                |      1.6 KB |   0.23 % |     16.4 KB |      4.1 KB |
| jar | taoensso/timbre/appenders/core.cljs                                              |      1.6 KB |   0.22 % |      7.4 KB |      5.1 KB |
| jar | com/cognitect/transit/eq.js                                                      |      1.4 KB |   0.20 % |      5.8 KB |      5.8 KB |
| jar | com/fulcrologic/fulcro/algorithms/tempid.cljc                                    |      1.3 KB |   0.18 % |      9.7 KB |      4.4 KB |
| jar | cognitect/transit.cljs                                                           |      1.3 KB |   0.18 % |     42.8 KB |     12.8 KB |
| jar | com/fulcrologic/fulcro/algorithms/do_not_use.cljc                                |      1.2 KB |   0.17 % |     10.9 KB |      5.9 KB |
| jar | goog/string/stringformat.js                                                      |      1.1 KB |   0.16 % |      7.8 KB |      7.8 KB |
| npm | object-assign                                                                    |    993.0 B  |   0.14 % |    994.0 B  |      2.2 KB |
| jar | goog/async/nexttick.js                                                           |    985.0 B  |   0.14 % |      9.4 KB |      9.4 KB |
| jar | cljs/core/async/impl/protocols.cljs                                              |    968.0 B  |   0.14 % |     14.7 KB |      1.8 KB |
| jar | com/fulcrologic/fulcro/rendering/keyframe_render.cljc                            |    867.0 B  |   0.12 % |      7.6 KB |      3.0 KB |
| jar | com/fulcrologic/fulcro/rendering/ident_optimized_render.cljc                     |    836.0 B  |   0.12 % |     30.4 KB |      7.4 KB |
| jar | goog/object/object.js                                                            |    733.0 B  |   0.10 % |     22.1 KB |     22.1 KB |
| jar | cljs/core/async.cljs                                                             |    687.0 B  |   0.10 % |    280.7 KB |     31.9 KB |
| jar | clojure/walk.cljs                                                                |    686.0 B  |   0.10 % |      5.9 KB |      3.6 KB |
| jar | com/fulcrologic/fulcro/algorithms/transit.cljc                                   |    684.0 B  |   0.10 % |     11.9 KB |      6.0 KB |
| jar | goog/array/array.js                                                              |    673.0 B  |   0.10 % |     59.8 KB |     59.8 KB |
| jar | goog/structs/simplepool.js                                                       |    566.0 B  |   0.08 % |      5.4 KB |      5.4 KB |
| jar | goog/structs/structs.js                                                          |    561.0 B  |   0.08 % |     11.3 KB |     11.3 KB |
| jar | goog/iter/iter.js                                                                |    557.0 B  |   0.08 % |     44.7 KB |     44.7 KB |
| jar | com/fulcrologic/fulcro/algorithms/scheduling.cljc                                |    551.0 B  |   0.08 % |      5.2 KB |      2.6 KB |
| jar | goog/disposable/disposable.js                                                    |    488.0 B  |   0.07 % |      9.3 KB |      9.3 KB |
| jar | shadow/js.js                                                                     |    480.0 B  |   0.07 % |      3.2 KB |      3.2 KB |
|     | my_app.cljs                                                                      |    480.0 B  |   0.07 % |      2.4 KB |    557.0 B  |
| jar | goog/string/string.js                                                            |    465.0 B  |   0.07 % |     47.6 KB |     47.6 KB |
| jar | com/cognitect/transit/util.js                                                    |    462.0 B  |   0.07 % |      4.9 KB |      4.9 KB |
| jar | cljs/tools/reader.cljs                                                           |    462.0 B  |   0.07 % |     81.3 KB |     33.9 KB |
| jar | goog/string/internal.js                                                          |    416.0 B  |   0.06 % |     12.3 KB |     12.3 KB |
| jar | taoensso/truss/impl.cljs                                                         |    389.0 B  |   0.06 % |     38.1 KB |     13.5 KB |
| jar | com/cognitect/transit/types.js                                                   |    361.0 B  |   0.05 % |     37.1 KB |     37.1 KB |
| jar | goog/string/stringbuffer.js                                                      |    354.0 B  |   0.05 % |      2.3 KB |      2.3 KB |
| jar | goog/uri/utils.js                                                                |    335.0 B  |   0.05 % |     38.1 KB |     38.1 KB |
| jar | goog/labs/useragent/browser.js                                                   |    326.0 B  |   0.05 % |     11.8 KB |     11.8 KB |
| jar | expound/paths.cljc                                                               |    308.0 B  |   0.04 % |     36.5 KB |      5.5 KB |
| jar | cljs/core/async/impl/dispatch.cljs                                               |    293.0 B  |   0.04 % |      2.0 KB |      1.3 KB |
| jar | com/fulcrologic/fulcro/algorithms/lookup.cljc                                    |    280.0 B  |   0.04 % |      3.0 KB |      2.8 KB |
| jar | goog/labs/useragent/platform.js                                                  |    212.0 B  |   0.03 % |      5.2 KB |      5.2 KB |
| jar | goog/dom/dom.js                                                                  |    156.0 B  |   0.02 % |    116.7 KB |    116.7 KB |
| jar | goog/functions/functions.js                                                      |    121.0 B  |   0.02 % |     16.7 KB |     16.7 KB |
| jar | goog/labs/useragent/util.js                                                      |    118.0 B  |   0.02 % |      3.9 KB |      3.9 KB |
| jar | cljs/tools/reader/impl/errors.cljs                                               |    108.0 B  |   0.02 % |     25.1 KB |      6.9 KB |
| jar | goog/labs/useragent/engine.js                                                    |    103.0 B  |   0.01 % |      4.4 KB |      4.4 KB |
| jar | com/fulcrologic/fulcro/algorithms/tx_processing/synchronous_tx_processing.cljc   |    103.0 B  |   0.01 % |     84.3 KB |     21.5 KB |
|     | shadow/module/main/append.js                                                     |     85.0 B  |   0.01 % |    110.0 B  |    110.0 B  |
| jar | goog/useragent/useragent.js                                                      |     80.0 B  |   0.01 % |     17.8 KB |     17.8 KB |
| jar | cljs/tools/reader/reader_types.cljs                                              |     78.0 B  |   0.01 % |     38.8 KB |      8.9 KB |
| jar | goog/useragent/product.js                                                        |     43.0 B  |   0.01 % |      4.9 KB |      4.9 KB |
| jar | cljs/tools/reader/edn.cljs                                                       |     43.0 B  |   0.01 % |     34.6 KB |     15.3 KB |
| jar | cljs/tools/reader/impl/utils.cljs                                                |     37.0 B  |   0.01 % |     18.7 KB |      2.7 KB |
| jar | cljsjs/react.cljs                                                                |      8.0 B  |   0.00 % |     90.0 B  |     88.0 B  |
```
