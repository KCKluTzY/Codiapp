import { Stack } from "expo-router";

export default function RootLayout() {
  // Layout racine pour les routes helper — header natif désactivé
  return <Stack
    screenOptions={{
      headerShown: false

    }} />;
}
