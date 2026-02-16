import { FlatList, View, Text, StyleSheet, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

import AdminHelperCard from "@/components/AdminHelperCard"; // composant custom pour afficher chaque aide
import { Helper } from "@/types/Helper"; // type TypeScript pour s'assurer de la structure d'un "helper"

// données simulées pour les aidants (mock data)
const HELPERS: Helper[] = [
    {
        id: "1",
        name: "Marie Dupont",
        maxDistance: 10, // distance max en km qu'elle est prête à parcourir
        status: "available", // disponibilité
        helpsThisMonth: 8, // nombre d'aides effectuées ce mois
    },
    {
        id: "2",
        name: "Lucas Martin",
        maxDistance: 5,
        status: "unavailable",
        helpsThisMonth: 2,
    },
];

export default function HelpersScreen() {
    const router = useRouter(); // hook pour la navigation avec expo-router

    return (
        <SafeAreaView style={{ flex: 1 }}>
            {/* Header de l'écran */}
            <View style={styles.header}>
                {/* Bouton pour revenir à l'écran précédent */}
                <Pressable onPress={() => router.back()} hitSlop={12}>
                    <Ionicons name="arrow-back" size={26} color="#111" />
                </Pressable>

                {/* Titre centré */}
                <Text style={styles.title}>Gérer les aidants</Text>

                {/* Spacer invisible pour que le titre reste centré malgré le bouton back */}
                <View style={{ width: 26 }} />
            </View>

            {/* Liste des aidants */}
            <FlatList
                data={HELPERS} // source de données
                keyExtractor={(item) => item.id} // clé unique pour chaque élément
                renderItem={({ item }) => (
                    // rendu de chaque élément via le composant custom AdminHelperCard
                    <AdminHelperCard helper={item} />
                )}
                contentContainerStyle={styles.list} // padding autour de la liste
                ItemSeparatorComponent={() => <View style={{ height: 12 }} />} // espace entre les éléments
            />
        </SafeAreaView>
    );
}

// Styles de l'écran
const styles = StyleSheet.create({
    header: {
        flexDirection: "row", // éléments alignés horizontalement
        alignItems: "center", // centrage vertical
        justifyContent: "space-between", // espace entre back button, titre et spacer
        paddingHorizontal: 16, // padding sur les côtés
        paddingBottom: 12, // padding sous le header
    },
    title: {
        fontSize: 18,
        fontWeight: "700", // titre en gras
    },
    list: {
        padding: 16, // padding autour de la FlatList
    },
});
