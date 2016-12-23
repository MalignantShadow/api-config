#api-config

An abstract configuration library.

# Problem
Some configurations systems (YAML, JSON) have very similar concepts, but displayed in different ways. This mainly comes in the form of Sections and Sequences and Pairings.
Sections contain Parirings; Pairings are simply map entries that hold the key (typically a string) and a value. A Sequence is essentially an untyped list or array that can hold
any number of any type of supported value.

## Is there a problem with having multiple configuration types?
No. It all comes down to taste and usage. It's up to the developer to use what they feel like. The problem lies in libraries. There will, or course, be more libraries depending
on the programming language, so let's just focus on one: Java.

The more config languages a developer uses, the more libraries the end up needing. The parsing bit is fine, that is going to be needed regardless. The conflict arises in the custom Objects.

For instance, a JSON library might specify a `JSONObject` and `JSONArray`, while a YAML library might specify a `YAMLSection` and `YAMLSequence`. These are actually the same thing.
Of course, the YAML Specification is very lenient and advanced in the way it can be parsed into custom objects; however - most simply use it with primitives/scalars.

Here's a neat experiment - give a JSON file to a YAML parser, what happens? You get a completely valid YAML Document. Why? Turns out JSON is valid YAML. So why have two separate libraries
for the representation of their data?

# Solution
The solution is to create an abstract library that represents *ANY* Configuration type. The parsing/emitting is completely isolated from the document/config representation itself. A neat
side-effect to this, is that you can then give the config representation to any processor a get a document of that type. For instance, fetch a YAML document and give it to a JSON emitter. No
data is lost, and you just safely and easily converted YAML to JSON (this can be done in a few lines, even one if you're not too picky)

# Dependencies
* [api-util](//www.github.com/MalignantShadow/api-util)
* [SnakeYAML](//www.bitbucket.org/asomov/snakeyaml)