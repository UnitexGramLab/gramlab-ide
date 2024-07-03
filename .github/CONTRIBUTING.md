## Contributing

> Unitex/GramLab project decision-making is based on a community meritocratic process. Anyone with an interest in Unitex/GramLab can join the community, contribute to the project design and participate in decisions. See http://unitexgramlab.org/how-to-contribute for more detailed information on how to start contributing to Unitex/GramLab.

You are welcome to contribute by [forking this repository](https://help.github.com/articles/fork-a-repo/)
and [sending pull requests](https://help.github.com/articles/using-pull-requests/)
with your changes. 

### Learn Git

If you want to contribute to this project, you need first learn about git. Here are some great resources to learn git and GitHub:

- https://gitimmersion.com
- https://learngitbranching.js.org/
- http://marklodato.github.io/visual-git-guide/index-en.html
- http://ndpsoftware.com/git-cheatsheet.html
- https://github.com/progit/progit2/releases/latest/download/progit.pdf

### Recommended Git Workflow

The recommended workflow to contribute is:

1. [Fork us](https://github.com/UnitexGramLab/gramlab-ide/fork)

1. **Clone** your fork locally

    ```
    git clone https://github.com/YOUR_GITHUB_USERNAME/gramlab-ide.git
    ```

1. Configure the **upstream** remote. To do this, add the remote location of the main
   `gramlab-ide` repository under the name `upstream`. This will allow you later
   to keep your fork up to date

    ```
    git remote add upstream git://github.com/UnitexGramLab/gramlab-ide.git
    git fetch upstream
    ```

1. Create a local **branch** for your changes

    ```
    git checkout -b my-changes origin/master
    ```

   Use a short and descriptive name for your branch. If you plan to develop a new
   feature or enhancement, name your branch as `feature/DESCRIPTIVE-NAME`, if
   you plan to fix a bug, name your branch as `fix/N` where `N` corresponds to
   an [issue number](https://github.com/UnitexGramLab/gramlab-ide/issues),
   e.g. `fix/5`

1. Install and test the IDE

   To install and test the IDE you need first to download the [Unitex Core][unitex-core] 
   executable (`UnitexToolLogger`). The easiest way to do this is to grab a full 
   [Unitex/GramLab release](https://unitexgramlab.org/releases/latest-stable/) 
   for your platform. After this you should do:

    ```
    cd gramlab-ide
    export UNITEX_BUILD_RELEASE_DIR=/path/to/unitexgramlab-release
    ant install
    ```

   Before testing,
   -   make sure that `UnitexToolLogger` is 
   located at `/path/to/unitexgramlab-release/App/UnitexToolLogger`,
   - download one or several of the available languages directly
   from https://unitexgramlab.org/releases/latest-stable/lingua/
   into `/path/to/unitexgramlab-release/`, as sibling directories to `App`.

1. For non-trivial changes, if it doesn't already exist, create a
   [**new issue**](https://github.com/UnitexGramLab/gramlab-ide/issues/new)

1. Edit the files and **compile** your code following the *How to Build* instructions

1. Make sure git knows your name and email address, e.g.

    ```
    git config --global user.name "John Doe"
    git config --global user.email "john.doe@example.org"
    ```

1. **Commit** your code following the [Commit Message Guidelines](https://github.com/UnitexGramLab/unitex-doc-contributor-guidelines/blob/master/pages/05.guidelines/01.commits/docs.md)

1. Make sure your fork is **up to date**

    ```
    git checkout master
    git pull upstream master
    ```

1. [**Rebase**](https://www.atlassian.com/git/tutorials/rewriting-history/git-rebase-i) your local branch

    ```
    git checkout my-changes
    git rebase master
    ```

1. **Push** your changes to your remote repository on GitHub

    ```
    git push origin
    ```

1. Go to ``https://github.com/YOUR_GITHUB_USERNAME/gramlab-ide`` and [Request a pull](https://github.com/UnitexGramLab/gramlab-ide/pulls)

[unitex]:  https://unitexgramlab.org
[unitex-core]: https://github.com/UnitexGramLab/unitex-core
