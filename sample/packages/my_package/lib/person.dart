import 'package:freezed_annotation/freezed_annotation.dart';

part 'person.freezed.dart';

@freezed
class Person with _$Person {
  final String firstName;
  final String lastName;
  final int age;

  Person({
    required String this.firstName,
    required String this.lastName,
    required int this.age,
  });
}