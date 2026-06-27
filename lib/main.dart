import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext MainContext) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('BUT Network'),
        ),
        body: const Center(
          child: Text('Welcome to BUT Network App'),
        ),
      ),
    );
  }
}
