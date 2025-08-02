import 'package:freezed_annotation/freezed_annotation.dart';

@freezed
class SomeDto with _$SomeDto {
  final String field1;
  final String field2;

  Person({
    required String this.field1,
    required String this.field2,
  });
}
