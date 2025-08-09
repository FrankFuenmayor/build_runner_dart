// ignore_missing_part: user.g.dart;
import 'package:freezed_annotation/freezed_annotation.dart';

part 'user.freezed.dart';

@freezed
class User with _$User {
  const factory User({
    required String name,
    required String handle,
  }) = _User;
}
