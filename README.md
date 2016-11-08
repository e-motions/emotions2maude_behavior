# e-Motions Behavior to Maude transformation *(using Java!)*

This file is intended to document the transformation from e-Motions behavior model to a Maude model.

## How to install and contribute to this project

Unfortunately, we cannot build this project using Maven, since we depend on the Behavior (e-Motions),
GCS (e-Motions) and Maude metamodels. Such metamodels have been created by us and they are not available
in a Nexus repository so far. That's why we packed this projects with such `.jar`s. However,
there still is a dependency to be solve: [the EMF framework](http://www.eclipse.org/modeling/emf/downloads/).

Once the project has been cloned, it can be imported using an Eclipse with the EMF framework installed.

## Transformation elements

### Instantaneous rules

Those behavior rules which have `minDuration` set to 0 are Instantaneous and they (i) do not create an action and (ii) are performed consuming 0 time.

For each of these rules we will create:
1. A **MTE**.
2. A **Rule**.
3. Zero or more [**NACs**](#NAC-patterns).

Here we explain how to create the rule.

The rule has as label the name of the e-Motions rule concatenated with the string `@Instantaneous`.
The left-hand side (LHS) has the model to be match. It is created by the Java class `ModelLHS`.
The right-hand side (RHS) has the `readjust` operator.

The `readjust` operator performs the deletion of objects that are specified in the in-place rule to be
deleted. This is because when an object is deleted more computations than simply not including that object
in the RHS is needed. Since contained objects or dangling references should be deleted too. The `readjust`
operation is also responsible of aborting those actions being performed if any of its participants is deleted.
The `readjust` operator has three arguments:
1. The deleted elements.
2. The deleted links.
3. The RHS model.

If there is neither deleted objects nor links, it means that we do not need the readjust operator.

#### `Update` operation for links

The `update` operation has four arguments:
1. The reference name
2. The variable with the value of the structural feature
3. The list of links to delete
4. The list of links to add

### NAC patterns

NAC patterns are present in the behavior rules. There might be zero or more NACs per rule. The Java class `CreateNac` creates all the elements needed to deal with them in Maude.

A NAC consists of:

1. An operation with the name of the nac and the rule `<nac>@<rule>` and parameters
`Set Set{@Object} @Model -> Bool` is created. Java method `createNACoperator` creates this.
2. Two equations are added:
    1. The *positive* equation with the following arguments (Java method `createConditionEquation`):
      - The set of objects and action execution `Oid`s in the LHS of the rule.
        - If there is no objects or actions in the LHS of the rule, a `empty` term is
        created by the method `getConstantEmpty` in the `MyMaudeFactory` class.
        - If there is an object or action in the rule's LHS, a variable is created
        using the `PatternElOid` Java class.
        - If there are more than one object or/and action, several variables are created using the `ManyPatternElOid` Java class.
      - The set of variables involved in the rule. Each variable is a Maude object.
        - If no variables are involved, it should create a `none` term. Created by
        the `getConstantNone` method in `MyMaudeFactory` Java class.
        - If there is *one* variable, such term is created by the `CreateVariable`
        Java class.
        - If more than one variable should be created, the class `CreateSetVariables`
        is used.
      - The elements involved in the NAC, both objects and action executions. This
      is the most complex part of the NAC, since we have to create the pattern of the
      NAC here. The main Java class is `PatternNAC`. This class creates a metamodel
      of the form `MM { Model }`, where `Model` will be a set of the objects and
      actions that appear in the NAC pattern. Moreover we will add a variable to be
      matched with the rest of the current state. As for the `Model`, we need to
      provide all the objects and action executions:
        - *Objects*: The Java class `Object2RecTermLHS` is intended to create such
        objects. An object is a `RecTerm` with the operator `<_:_|_>`, with a variable
        for the Oid with type `OCL-Type`, a variable for the class with type its class (thus allowing inheritance), and a set of structural features. The set of
        structural features depends on whether there is any slot or out link in the
        behavior object. If any or if not, we also add a variable of type
        `Set{@StructuralFeatureInstance}`  to match all the remaining structural features.
        For the out links and slots, we use the Java class `ObjectStructFeatLHS`. The interested
        reader could go to the section dedicated to this matter.

    2. The *negative* equation with the following arguments (Java method `createOwiseEquation`):
      - A variable `OIDSET@:Set`.
      - A variable `OBJSET@:Set{@Object}`.
      - A variable `MODEL@:@Model`.
      And `false` as result. This is the `owise` equation.

### Representing an e-Motions LHS object in Maude

A Maude object is represented by the operator `<_:_|_> : Oid Cid StructuralFeatures -> Object`. The Java class `Object2RecTermLHS` is intended to generate this term.
The Java class `Object2RecTermLHS` generates a Maude RecTerm with the object operator and with the variable for the Oid and the Cid as it is mentioned below. For the structural features, the Java class ``
- **Oid**: For the `Oid` we create a variable to be matched with the object. Such variable
has the same name as the behavior object has. As for the sort, we use the `OCL-Type`.
- **Cid**: For the `Cid` we use a variable which is the class name in upper case and each package is appended using the `@` symbol. The sort of such variable is the Maude sort used for such class.
- **Structural Features**: The java class `ObjectStructFeatLHS` creates this term. We distinguish whether the
object has structural features or not, meaning that it has out links and/or slots. If it has not, we only creates a output variable to be match with its structural features. Provided it has out links and/or slots, we proceed as the following:
  - *Slots*: It has been done with a variable. As it has been done in the ATL transformation.
  - *Links*: Much casuistry could appear in the creation of those structural features which are links. here we itemize all those cases:
    - *Only one link whose reference is a OrderedSet without `pos` attribute*: it can be specified in the matching of the rule.
    - *Only one link whose reference is a Sequence without `pos` attribute*: it can be specified in the matching of the rule.
    - *One or more links whose reference is a Set*: If `pos` has been set, an exception is thrown. It can be specified using the matching engine.
    - *One or more links whose reference is a Bag*: If `pos` has been set, an exception is thrown. It is specified using the matching.
    - *Others*: A variable is created and afterwards it will be checked using mOdCL.

*Branstorming*. `Oid`s are represented as OCL-Type variables when used in Maude objects.

### Optimizations

## Code structure

### Coarse Rules
**CoarseRule** is a class which returns one or more `ModElement`s, i.e. `Operation`s, `Equation`s or `Rule`s.

#### 1. Links


## TO-DO

1. Why is needed to have into account the opposite links? maybe the readjust to set
them up to null if deleted?
2. Conditional NACs

### little to-dos
- do not have singleton instances, they depend on the transformations.
- change get by create in MyMaudeFactory.
- opposite links?

### little dones
- `EmotionsModule`: change getters for hashmap with names.
