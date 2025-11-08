import {Link, useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import api from "../utils/axios";
import BookingWidget from "../BookingWidget";
import PlaceGallery from "../PlaceGallery";
import AddressLink from "../AddressLink";

export default function PlacePage() {
    const {id} = useParams();
    const [place, setPlace] = useState(null);

    useEffect(() => {
        if (!id) return;
        api.get(`/businessCompany/places/${id}`).then(response => {
            setPlace(response.data);
        });
    }, [id]);

    if (!place) return '';

    return (
        <div className="mt-4 bg-gray-100 -mx-8 px-8 pt-8">
            <h1 className="text-3xl">{place.title}</h1>
            <AddressLink>{place.address}</AddressLink>
            <PlaceGallery place={place} />

            <div className="mt-8 mb-8 grid gap-8 grid-cols-1 md:grid-cols-[2fr_1fr]">
                <div>
                    <div className="my-4">
                        <h2 className="font-semibold text-2xl mb-2">Description</h2>
                        <p className="text-gray-700">{place.description}</p>
                    </div>

                    {place.perks && place.perks.length > 0 && (
                        <div className="my-6">
                            <h2 className="font-semibold text-2xl mb-2">Perks</h2>
                            <div className="flex flex-wrap gap-3">
                                {place.perks.map((perk, index) => (
                                    <span
                                        key={index}
                                        className="bg-white border border-gray-300 rounded-full px-4 py-2 text-sm text-gray-700 shadow-sm"
                                    >
                    {perk.charAt(0).toUpperCase() + perk.slice(1)}
                  </span>
                                ))}
                            </div>
                        </div>
                    )}

                    <div className="my-6">
                        <p>Check-in: {place.checkIn}:00</p>
                        <p>Check-out: {place.checkOut}:00</p>
                        <p>Max number of guests: {place.maxGuests}</p>
                        <p>Price per night: ${place.price}</p>
                    </div>
                </div>

                <div>
                    <BookingWidget place={place} />
                </div>
            </div>

            <div className="bg-white -mx-8 px-8 py-8 border-t">
                <h2 className="font-semibold text-2xl mb-2">Extra Info</h2>
                <p className="text-sm text-gray-700 leading-5">{place.extraInfo}</p>
            </div>
        </div>
    );
}
