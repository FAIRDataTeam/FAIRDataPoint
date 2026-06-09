# Contributing

Before contributing to this repository, please discuss the intended change with the repository owners.
This can be done via [issues], [discussions], email, or any other available method.

Please note we have a code of conduct, please follow it in all your interactions with the project.

>[!IMPORTANT]
>Code generated using any form of AI, LLM, or similar tools, ***MUST*** be clearly labeled as such in the PR.

## Version control workflow

Our version control workflow is pragmatic, aiming to minimize overhead for a small team.
It resembles [github flow] with some aspects of [git flow] and [trunk-based development], but does not really fit well in any of those boxes.

- We have a single `master` branch which is supposed to remain stable. 
- For every *significant* change, we create a new short-lived branch from `master`.
- We immediately create a pull request (PR) for the new short-lived branch.  
- We push often, at least once a day.
  This allows us to keep track of work in progress and provide guidance before things get off track.
- After merging back into `master`, the short-lived branch is deleted.
- Rebasing and force pushing the *short-lived* branch is allowed only *if* the corresponding PR does not have any comments yet. 
  This ensures the comments remain in context.
- Releases are created directly from the `master` branch.
- Only the latest major release is supported.

Note that there is still a `develop` branch, but that is legacy.

## Pull requests are used for significant changes

All significant contributions should be added via [pull requests] (PRs).
This allows us to discuss and review the changes, and document design choices.
It also ensures that the contribution is mentioned in the auto-generated change logs.

A significant change is anything that is worth knowing about for developers or end users.
Tiny changes that do not directly affect the inner workings of the application, like fixing some typos in the readme or a comment, can sometimes be pushed directly onto the `master` branch.
Always ask yourself: Should this change appear in the change log?

## Pull request titles and labels are important

Pull requests form the basis for the change logs that are auto-generated during the release process.
For this reason, PR titles must be *concise* and *descriptive*.
When writing a PR title, remember that this title is the only thing users will see in the change log.
Also note that change log entries are categorised based on their *labels*, as defined in [release.yml].

## Pull requests must be focused

Individual PRs *must* have a strong focus which is clear from the title.
PRs should also have a clear description and rationale and should be linked to relevant issues, if any.

If a PR gets very large, split it up into smaller PRs that can be reviewed separately.

## Merge method is chosen depending on content

Depending on the size and type of PR, different [merge methods] can be applied.
We prefer a squash merge for small PRs, resulting in a single commit.
However, in some cases, it may be useful to keep the individual commits from the PR.
In that case we use a merge commit (as in `--no-ff`).

## External dependencies are minimised

To reduce the maintenance burden, we aim to minimise the number of external dependencies.
If external dependencies cannot be avoided, we prefer well-supported projects with large numbers of contributors.

## Semantic versioning applies to the API

Release versions are based on [semantic versioning], i.e. `major.minor.patch`.
However, the semantic versioning rules for `major` changes are only applied to changes in the HTTP API.
For example, breaking changes in application *configuration* may occur in `minor` versions, as long as these changes are not reflected in the HTTP API.

[discussions]: https://github.com/FAIRDataTeam/FAIRDataPoint/discussions
[git flow]: https://nvie.com/posts/a-successful-git-branching-model/
[github flow]: https://githubflow.github.io/
[issues]: https://github.com/FAIRDataTeam/FAIRDataPoint/issues
[merge methods]: https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/configuring-pull-request-merges/about-merge-methods-on-github
[pull requests]: https://github.com/FAIRDataTeam/FAIRDataPoint/pulls
[releases]: https://github.com/FAIRDataTeam/FAIRDataPoint/releases
[release.yml]: .github/release.yml
[semantic versioning]: https://semver.org
[trunk-based development]: https://trunkbaseddevelopment.com/
