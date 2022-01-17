# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.2] - 2019-04-15

### Changes
- CSV Serializer : add lang column

## [1.0.1] - 2018-05-31

### Changes
- GraphStore : set properties protected
- GraphStore : set constructor public
- TripletIterator : add stream method
- TripletIterator : set properties protected
- TripletIterator::forEachRemaining : refactor

## [1.0.0] - 2018-05-14

### Added
- Add setContext method to JsonLDSerializer to add @context terms and keywords
- JsonLDSerializer now supports @vocab keyword

### Changes
- Serializer : remove unused baseUri parameter
- Empty prefix is now forbidden in PrefixMapping : it automatically create a nsX prefix.

### Fixes
- XMLSerializer now use rdf:datatype instead of xml:datatype for datatype declarations

## [0.1.1] - 2018-03-20

### Added
- Fix XML serializer to handle better text escaping

## [0.1.0] - 2018-02-22

### Added
- First release of the library
