JSON COMPARATOR
======
    This library provides an intuitive and flexible means to compare JSON structures.  It was
    initially built with testing in mind.
     
    In order to compare JSON structures while allowing runtime variations, such as unique IDs
    stored in the JSON, a template JSON structure is specified together with optional comparison
    rules which alter the comparison of elements at specified paths in the JSON.


TO USE
===========
````
    JsonComparator comparator = new JsonComparatorBuilder().build();

    String comparisonSpec = "{ \"templateJson\": [ 1, 2, 3 ] }";
    String actualJson = "[ 1, 2, 3 ]";

    JsonComparatorResult result = comparator.compare(comparisonSpec, actualJson);

    assertTrue(result.isMatch());
    
    // Same thing, except include the error message on mismatches:
    assertTrue(result.getErrorMessage(), result.isMatch());
````


TO USE RULES
===========
````
    JsonComparator comparator = new JsonComparatorBuilder().build();

    String
        comparisonSpec =
        "{ \"rules\": [ { \"selector\": { \"path\": \"$[2]\" }, \"action\": \"matches\", \"pattern\": \"[3-5]\" } ], \"templateJson\": [ 1, 2, 3 ] }";
    String actualJson = "[ 1, 2, 4 ]";

    JsonComparatorResult result = comparator.compare(comparisonSpec, actualJson);

    assertTrue(result.getErrorMessage(), result.isMatch());
````



PATHS
===========
    Use the JsonPath library, here: https://github.com/jayway/JsonPath.

    Some examples:

        # Match any field named "random"
        $..['random']

        # Match the third element of any array
        $..[2]

        # Match an exact path
        $['team']['roster'][1]


RULE ACTIONS
===========
    "matches"
        - Regular expression matching of the value; best used only with primitives.
        - The "pattern" field for the rule contains the regular expression applied using Java's
          String.matches() method.

    "set"
        - Comparison of JSON arrays as sets, meaning order may vary.
        - The set of array entries in the template must match the set of array entries in the
          actual JSON; a deep comparison of the entries is performed.


EXAMPLE COMPARISON SPECIFICATION
===========
````
    {
      "rules": [
        {
          "pattern": "([0-9a-fA-F]{2,}-)*[0-9a-fA-F]{2,}",
          "action": "matches",
          "selector": {
            "path": "$..['uuid']"
          }
        }
      ],
      "templateJson": {
        "root": {
          "type": "alpha",
          "uuid": "XXX",
          "children": [
            {
              "type": "beta",
              "children": [
                {
                  "type": "omega",
                  "children": [
                    "one ",
                    "two",
                    {
                      "uuid": "XXX",
                      "name": "Ralph"
                    }
                  ]
                }
              ]
            }
          ]
        }
      }
    }
````

**The specification above matches the following**
````
    {
      "root": {
        "type": "alpha",
        "uuid": "A0661332-2FB4-4A3C-8C6D-075E10110FFC",
        "children": [
          {
            "type": "beta",
            "children": [
              {
                "type": "omega",
                "children": [
                  "one ",
                  "two",
                  {
                    "uuid": "5E660D8D-8D62-48C8-B0A2-1AE02A7D59A4",
                    "name": "Ralph"
                  }
                ]
              }
            ]
          }
        ]
      }
    }
````
