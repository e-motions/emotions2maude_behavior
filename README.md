# e-Motions Behavior to Maude transformation

This file is intended to document the transformation from e-Motions behavior model to a Maude model.


## NAC patterns

NAC patterns are present in the behavior rules. There might be zero or more NACs per rule. The Java class `CreateNac` creates all the elements needed to deal with them in Maude.

A NAC consists of:

1. An operation with the name of the nac and the rule `<nac>@<rule>` and parameters
`Set Set{@Object} @Model -> Bool` is created. Java method `createNACoperator` creates this.
2. Two equations are added:
    1. The *positive* equation with the following arguments (Java method `createConditionEquation`):
      - The set of objects and action execution `Oid`s in the LHS of the rule.
        We use  
      - The set of variables involved in the rule. Each variable is a Maude object.
      - The elements involved in the NAC, both objects and action executions.

      The creation of this equation involves several Java classes and elements.
    2. The *negative* equation with the following arguments (Java method `createOwiseEquation`):
      - A variable `OIDSET@:Set`.
      - A variable `OBJSET@:Set{@Object}`.
      - A variable `MODEL@:@Model`.
      And `false` as result. This is the `owise` equation.

### Representing an e-Motions LHS object in Maude
A Maude object is represented by the operator `<_:_|_> : Oid Cid StructuralFeatures -> Object`
- **Oid**: For the `Oid` we create a variable to be matched with the object. Such variable
has the same name as the behavior object has. As for the sort, we use the `OCL-Type`.
- **Cid**: For the `Cid` we use a variable which is the class name in upper case and each package is appended using the `@` symbol. The sort of such variable is the Maude sort used for such class.
- **StructuralFeatures**: The most complex part. We distinguish if StructuralFeatures are needed for such object or not. They are needed if output links or slots are present in the object.
  - *Links*:


*Branstorming*. `Oid`s are represented as OCL-Type variables when used in Maude objects.

### Optimizations

#### 1. Links


## TO-DO

1. Why is needed to have into account the opposite links? maybe the readjust to set
them up to null if deleted?

### little to-dos
- do not have singleton instances, they depend on the transformations.
- change get by create in MyMaudeFactory.
- opposite links?

### little dones
- `EmotionsModule`: change getters for hashmap with names.
