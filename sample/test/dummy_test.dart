import 'package:mockito/annotations.dart';
import 'my_dummy_class.dart';

@GenerateMocks([MyDummyClass])
@GenerateNiceMocks([MockSpec<MyDummyClass>(as: #MyNiceDummyClass)])
void mocks() {}
