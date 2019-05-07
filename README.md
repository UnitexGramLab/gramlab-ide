# Unitex/GramLab IDE  [![Build Status](https://travis-ci.org/UnitexGramLab/gramlab-ide.svg?branch=master)](https://travis-ci.org/UnitexGramLab/gramlab-ide)

> [Unitex/GramLab][unitex] is an open source, cross-platform, multilingual, lexicon- and grammar-based corpus processing suite

GramLab is the Integrated Development Environment (IDE) of [Unitex/GramLab][unitex].

<p align="center">
  <img src="http://www-igm.univ-mlv.fr/~unitex/img/gramlab-ide.png" width="70%" height="70%" alt="GramLab IDE"/>
</p>

## How to Build

    git clone https://github.com/UnitexGramLab/gramlab-ide
    cd gramlab-ide
    ant

## How to Install and Test

To install and test the IDE you need first to download the [Unitex Core][unitex-core] 
executable (`UnitexToolLogger`). The easiest way to do this is to grab a full 
[Unitex/GramLab release](https://unitexgramlab.org/releases/latest-stable/) 
for your platform. After this you should do:

    cd gramlab-ide
    export UNITEX_BUILD_RELEASE_DIR=/path/to/unitexgramlab-release
    ant install

If you encounter a problem, make sure that `UnitexToolLogger` is 
located at `/path/to/unitexgramlab-release/App/UnitexToolLogger`.

## Documentation

User's Manual (in PDF format) is available in English and French([more
translations are welcome](https://github.com/UnitexGramLab/unitex-doc-usermanual)).
You can view and print them with Evince,
downloadable [here](https://wiki.gnome.org/Apps/Evince/Downloads). The
latest on-line version of the User's Manual is accessible
[here](http://releases.unitexgramlab.org/latest-stable/man/).

## Support

Support questions can be posted in the [community support
forum](http://forum.unitexgramlab.org). Please feel free to submit any
suggestions or requests for new features too. Some general advice about
asking technical support questions can be found
[here](http://www.catb.org/esr/faqs/smart-questions.html).

## Reporting Bugs

See the [Bug Reporting
Guide](http://unitexgramlab.org/index.php?page=6) for information on
how to report bugs.

## Governance Model

Unitex/GramLab project decision-making is based on a community
meritocratic process, anyone with an interest in it can join the
community, contribute to the project design and participate in
decisions. The [Unitex/GramLab Governance
Model](http://governance.unitexgramlab.org) describes
how this participation takes place and how to set about earning merit
within the project community.

## Spelling

Unitex/GramLab is spelled with capitals "U" "G" and "L", and with
everything else in lower case. Excepting the forward slash, do not put
a space or any character between words. Only when the forward slash
is not allowed, you can simply write “UnitexGramLab”.

It's common to refer to the Unitex/GramLab Core as "Unitex", and to the
Unitex Project-oriented IDE as "GramLab". If you are mentioning the
distribution suite (Core, IDE, Linguistic Resources and others bundled
tools) always use "Unitex/GramLab".

## Contributing

We welcome everyone to contribute to improve this project. See [CONTRIBUTING.md](.github/CONTRIBUTING.md) 
for contribution guidelines and instructions.

## License

<a href="/LICENSE"><img height="48" align="left" src="http://www.gnu.org/graphics/empowered-by-gnu.svg"></a>

This program is licensed under the [GNU Lesser General Public License version 2.1](/LICENSE). 
Contact unitex-devel@univ-mlv.fr for further inquiries.

---

Copyright (C) 2019 Université Paris-Est Marne-la-Vallée

[unitex]:  http://unitexgramlab.org
[unitex-core]: https://github.com/UnitexGramLab/unitex-core
